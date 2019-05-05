#!/bin/sh

PKGS="\
github.com/winded/passu-mobile/passu_mobile \
github.com/winded/passu-mobile/passu_mobile/util
"

OUTPUT="$GOPATH/src/github.com/winded/passu-mobile/android/go-libs/passu_mobile.aar"

gomobile bind -o "$OUTPUT" -javapkg="me.winded.passu" -target=android $PKGS
