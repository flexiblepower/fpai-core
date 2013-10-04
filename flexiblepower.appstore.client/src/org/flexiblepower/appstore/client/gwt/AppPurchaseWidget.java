package org.flexiblepower.appstore.client.gwt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.flexiblepower.appstore.client.shared.AppStoreItem;
import org.flexiblepower.appstore.client.shared.AppStoreQuestion;
import org.flexiblepower.appstore.client.shared.AppStoreServiceAsync;
import org.flexiblepower.appstore.client.shared.AppStoreStatus;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AppPurchaseWidget extends FlowPanel {

    private static final String TXT_PURCHASE = "Purchase";
    private static final String TXT_CONFIRM_QUESTIONS_HEADER = "This app needs the following permissions to operate.  Please indicate if you wich to grant the following permissions: ";
    private static final String TXT_ALERT_COULDNOTCONTACTAPPSTORE = "Could not contact App Store, please try again later ...";

    // to replace with locale based resource
    private static String getText(String textFragmentIdentifier) {
        return textFragmentIdentifier;
    }

    private final AppStoreItem application;
    private final AppStoreServiceAsync appStoreServiceAsync;

    private final Timer refreshTimer;
    private final ParagraphPanel statusParagraph;
    private final ParagraphPanel priceLabel;
    private final Anchor purchaseButton;

    public AppPurchaseWidget(final AppStoreItem application, AppStoreServiceAsync appStoreService) {
        this.application = application;
        appStoreServiceAsync = appStoreService; // GWT.create(AppStoreService.class);

        refreshTimer = new Timer() {
            @Override
            public void run() {
                appStoreServiceAsync.getStatus(application.getId(), new AsyncCallback<AppStoreStatus>() {
                    @Override
                    public void onSuccess(AppStoreStatus status) {
                        if (status != null) {
                            updateStatus(status);
                            if (status.getStatus() < 100) {
                                schedule(1000);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable tr) {
                    }
                });
            }
        };

        add(new HeaderPanel(3, application.getName()));
        add(new ParagraphPanel(application.getDescription()));

        statusParagraph = new ParagraphPanel("");
        add(statusParagraph);

        // price
        priceLabel = new ParagraphPanel("Free");
        if (application.getPrice() != null) {
            priceLabel.setText(SafeHtmlUtils.fromTrustedString("&euro; " + application.getPrice()));
        }
        priceLabel.setStyleName("priceLabel");
        add(priceLabel);

        // icon
        // TODO

        // buttons
        // TODO String purchaseButtonText = (application.isAlreadyPurchased() ? getText(TXT_REINSTALL) :
        // getText(TXT_PURCHASE)) + " ...";
        String purchaseButtonText = TXT_PURCHASE + "...";

        purchaseButton = new Anchor();
        Element a = purchaseButton.getElement();
        a.addClassName("button green");
        a.setInnerText(purchaseButtonText);

        // TODO purchaseButton.setEnabled(application.isPurchaseable());

        purchaseButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                handlePurchaseIntent(application.getId());
            }
        });
        add(purchaseButton);

        setStyleName("large_tile white");
    }

    /**
     * dialog to present permission questions, and confirm purchase with answers
     */
    class ConfirmationDialog extends DialogBox {
        public ConfirmationDialog(final List<AppStoreQuestion> questions) {
            super(false, true);

            FlowPanel panel = new FlowPanel();
            add(panel);

            // panel.add(new CaptionPanel("Permissions for " + application.getName()));

            Label topText = new Label(TXT_CONFIRM_QUESTIONS_HEADER);
            panel.add(topText);

            UnsortedList permissionList = new UnsortedList();
            for (AppStoreQuestion question : questions) {
                if (question.isShown()) {
                    permissionList.add(new Label(question.getQuestion()));
                }
            }
            panel.add(permissionList);

            FlowPanel buttons = new FlowPanel();
            Anchor okButton = new Anchor("Yes, install");
            okButton.setStyleName("okButton button");
            Anchor cancelButton = new Anchor("Cancel");
            cancelButton.setStyleName("cancelButton button");
            buttons.add(okButton);
            buttons.add(cancelButton);
            panel.add(buttons);

            okButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    handlePurchaseConfirmation(questions);
                    hide();
                }
            });

            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            });

            setGlassEnabled(true);
        }
    }

    private void handlePurchaseIntent(final int itemId) {
        appStoreServiceAsync.getDetails(itemId, new AsyncCallback<List<AppStoreQuestion>>() {
            @Override
            public void onSuccess(List<AppStoreQuestion> permissionQuestions) {
                ConfirmationDialog dialog = new ConfirmationDialog(permissionQuestions);
                dialog.center();
                dialog.show();
            }

            @Override
            public void onFailure(Throwable arg0) {
                Window.alert(getText(TXT_ALERT_COULDNOTCONTACTAPPSTORE));
            }
        });
    }

    void handlePurchaseConfirmation(final List<AppStoreQuestion> answeredQuestions) {
        Set<String> acceptedPermissions = new HashSet<String>();
        for (AppStoreQuestion q : answeredQuestions) {
            acceptedPermissions.addAll(q.getCoveredPermissions());
        }

        appStoreServiceAsync.purchase(application.getId(), acceptedPermissions, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void v) {
                refreshTimer.schedule(1);
            }

            @Override
            public void onFailure(Throwable tr) {
                Window.alert("Error during purchace: " + tr.getMessage());
            }
        });
    }

    void updateStatus(AppStoreStatus status) {
        String text = status.getErrorMessage() == null ? status.getStatus() + "% - " + status.getCurrentAction()
                                                      : "Error during installation: " + status.getErrorMessage();
        statusParagraph.setText(SafeHtmlUtils.fromString(text));

        priceLabel.setVisible(false);

        if (status.getErrorMessage() != null) {
            purchaseButton.setText("Reinstall");
            purchaseButton.setVisible(true);
        } else {
            purchaseButton.setVisible(false);
        }
    }
}
