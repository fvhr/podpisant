import datetime

import pytz


def convert_to_moscow_time(value):
    moscow_tz = pytz.timezone('Europe/Moscow')

    if value is not None:
        if value.tzinfo is None:
            value = value.replace(tzinfo=pytz.utc)
        value = value.astimezone(moscow_tz)
        return value.replace(tzinfo=None)  # Удаляем информацию о часовом поясе
    return None


def convert_stamp_to_moscow_time(stamp: str | None, return_time=False):
    if stamp is None or stamp == 'None':
        stamp = str(datetime.datetime.now().timestamp())

    stump = int(stamp[:10])
    utc_time = datetime.datetime.utcfromtimestamp(stump).replace(
        tzinfo=datetime.timezone.utc
    )
    moscow_tz = pytz.timezone('Europe/Moscow')
    localized_time = utc_time.astimezone(moscow_tz)

    if return_time:
        return localized_time
    return localized_time.strftime('%Y-%m-%d %H:%M:%S')


def convert_to_serializable(data: dict, refactor=True) -> dict:
    if isinstance(data, dict):
        return {
            key: {
                k: (
                    str(convert_to_moscow_time(v))
                    if isinstance(v, datetime.datetime) and refactor
                    else str(v)
                )
                for k, v in value.items()
            }
            for key, value in data.items()
        }
