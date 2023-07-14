import flask
import functions_framework

from actions import scan_nita, scan_other


@functions_framework.http
def scan(request: flask.Request) -> flask.Response:
    """checks website for new notices

    Returns:
        JSON data of new notices since last run
    """

    data = request.get_json()
    action = data.get("action")

    if action == "nita":
        return scan_nita()
    elif action == "other":
        return scan_other()
    else:
        return flask.Response("{'error': 'Invalid \'action\' in JSON body'}", status=201, mimetype='application/json')
