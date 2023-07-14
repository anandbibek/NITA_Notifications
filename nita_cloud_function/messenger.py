import firebase_admin
from firebase_admin import messaging, credentials

from util import is_running_on_cloud

if is_running_on_cloud():
    firebase_admin.initialize_app()
else:
    cred = credentials.Certificate("secrets/something.json")
    firebase_admin.initialize_app(cred)


def send_notification(topic, title, body):
    """Send FCM notification to a specific topic"""
    message = messaging.Message(
        topic=topic,
        data={"message": body},
        notification=messaging.Notification(
            title=title,
            body=body
        )
    )

    print('Payload:', message)
    # Send a message to the devices subscribed to the provided topic.
    response = messaging.send(message)
    # Response is a message ID string.
    print('Successfully sent message:', response)
