FROM python:3

COPY requirements.txt /requirements.txt
RUN pip install -r /requirements.txt
COPY connect.py /connect.py
CMD [ "python3", "/connect.py" ]

