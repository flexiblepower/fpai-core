#!/bin/bash

echo flexiblepower.* | xargs -n 1 cp flexiblepower.ral/.checkstyle 2> /dev/null
echo flexiblepower.*/.settings/ | xargs -n 1 cp flexiblepower.ral/.settings/* 2> /dev/null
