
# SexyTopo

<img src="app/src/main/res/drawable-hdpi/laser_icon.png" width=10%><br> 

The SexyTopo cave and underground surveying app for Android. Next-gen surveying with a sexy interface.

This app is experimental software made available for free with no guarantee it is fit for anything in particular. I'm not responsible if it loses your data or leads you into some horrific adventure. Caving is a potentially dangerous activity etc. etc. - you know the score :)


# FAQ

## How does SexyTopo work?
SexyTopo is an app that runs on Android and talks to a laser measuring device over Bluetooth. SexyTopo builds up a skeleton of the cave using laser measurements and provides sketching tools to fill in the details. It does other things too.

## I'm interested in giving SexyTopo a go. What do I do?
You need an Android phone or tablet. The easiest way is to install SexyTopo via the Google Play app store. Just search for "SexyTopo" and click install. It's a very small download.
https://play.google.com/store/apps/details?id=org.hwyl.sexytopo

If you can't access Google Play for some reason (e.g. you're in China) then you should be able to get the APK from here: https://github.com/richsmith/sexytopo/tree/master/app/release

## What Android device is best to use with SexyTopo?
A large phone or "phablet" (as oppposed to a full-size tablet or small phone) seems to be the best form factor.
A Galaxy Note is a good choice. Apart from being about the right size, a proper stylus helps greatly in surveying. Consider buying second hand to get an affordable device for taking underground.

## What measuring instruments does SexyTopo work with?
Currently the following:

* The [DistoX2](https://paperless.bheeb.ch/) (several versions)
* The [Shetland Attack Pony 5](https://www.shetlandattackpony.co.uk/)
* The [Shetland Attack Pony 6](https://www.shetlandattackpony.co.uk/)
* The [Bric4](https://www.caveexploration.org/gear/bric4)
* Analogue instruments (if you input the data by hand, though not really recommended)

Please get in touch if think you can help SexyTopo work with any other instruments.

## Does SexyTopo need all those permissions?
Yes, even if it might not be obvious what they do. E.g. asking for certain Bluetooth permissions requires asking for location permissions (presumably you can use Bluetooth to find your location in some way). If you don't grant it everything it asks for then it may fail in unexpected ways.

## I'm interested in the SexyTopo project; can I help?
Awesome! Please get in touch. I'm interested in programmers, testers, ideas, etc.! There's also a Facebook group for updates about SexyTopo.
https://www.facebook.com/groups/sexytopo

If you are interested in contributing code (yay), I'd ideally prefer it if you talk to me before committing. I (or someone) might already be working on that feature, or there might be some other reason why your idea hasn't made it in yet. I'm likely to be enthusiastic 

## I've had this problem...
Please open a Github issue. You can also email me, ask on the FB page, or track me down in the pub, but Github isues are easiest to track.

If you get a crash (hopefully rare now), please make sure you connect your device to WiFi when you get a chance. A very basic error report will get uploaded if SexyTopo gets a chance (please also select "Send Error Report" for best results).

## What import / export options does SexyTopo have?
Sexytopo understands the following formats. If you are interested in helping SexyTopo understand other formats, please contact me.

### Export
* TherionSurvex (data)
* PocketTopo .txt export format
* SVG
* Compass (not written by me, haven't tested how well it works)

### Import
+ Therion (Therion files produced by SexyTopo and other simple Therion files)
+ Survex (Survex files produced by SexyTopo and other simple Survex files)
+ PocketTopo .txt export format
