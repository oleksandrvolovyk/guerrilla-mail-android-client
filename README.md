# Guerrilla Mail Android Client

This is an Android client for [Guerrilla Mail](https://www.guerrillamail.com), a disposable temporary email service. This app is powered by the Guerrilla Mail public API, which allows users to create and manage temporary email addresses.

<a href='https://play.google.com/store/apps/details?id=volovyk.guerrillamail&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' height="80"/></a>

## Features

- Get temporary email addresses
- Receive and view incoming emails

## Dependencies

- [Guerrilla Mail API](https://www.guerrillamail.com/GuerrillaMailAPI.html)
- [Retrofit](https://square.github.io/retrofit/)
- [OkHttp](https://square.github.io/okhttp/)
- [Jackson](https://github.com/FasterXML/jackson)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [Hilt](https://dagger.dev/hilt/)
- [Timber](https://github.com/JakeWharton/timber)

## Build Instructions

1. Clone the repository
2. Setup AdMob:
   1. Create an AdMob account 
   2. Set up a new app
   3. Create an interstitial ad unit
   4. Add your ids to keys.properties:
    ```
    ADMOB_APP_ID="ca-app-pub-xxx"
    ADMOB_TEST_AD_ID="ca-app-pub-3940256099942544/1033173712"
    ADMOB_MY_AD_ID="ca-app-pub-yyy"
    ```  
3. Open the project in Android Studio
4. Build and run the app

## Usage Instructions

1. Launch the app
2. Wait for the app to receive temporary email address from the API
3. Tap on the assigned email to copy it to the clipboard

## Credits

- [Guerrilla Mail](https://www.guerrillamail.com) for providing the public API

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.