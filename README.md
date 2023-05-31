# Alarm Android App

This is an Android alarm application that allows users to set an alarm and play a ringing sound at the specified time. The application uses fragments to create the user interface and a service to handle the alarm functionality. It also includes a broadcast receiver to stop the service and ringing when certain events occur.

## Requirements<a name="requirements"></a>
To run this application, you will need:
- Android Studio
- An Android device or emulator

## Installation<a name="installation"></a>
1. Clone this repository to your local machine using the following command:
```bash
git clone https://github.com/Mao-17/Alarm_Android_App.git
```
2. Open Android Studio and select "Open an Existing Project".
3. Navigate to the cloned repository and select the project folder.
4. Wait for Android Studio to build the project and resolve dependencies.

## Usage<a name="usage"></a>
1. Launch the application on your Android device or emulator.
2. The main screen will display a list of alarm timings.
3. To set a new alarm, click on the "+" button.
4. In the alarm creation screen, use the time picker to set the desired alarm time in hours and minutes.
5. Click the "Set Alarm" button to add the alarm to the list.
6. To start the alarm service, click the "Start Service" button.
7. The service will continuously check the current time every 10 seconds.
8. When the alarm time matches the current time, the specified music will start ringing, and a toast message will be shown.
9. The ringing will continue for 10 seconds.
10. The service will be automatically destroyed when the ringing stops, and a toast and log message will indicate that the service has stopped.
11. To stop the service manually, click the "Stop Service" button.
12. If the music was ringing, it will stop immediately.
13. Whenever the service starts or stops, the appropriate toast and log message will be displayed.
14. The service will work even when the app is minimized.
