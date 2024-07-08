FROM langchain/langchain

WORKDIR /app
RUN <<EOF
apt-get update
apt-get install -y build-essential curl software-properties-common
rm -rf /var/lib/apt/lists/*
EOF

WORKDIR /dependencies
COPY requirements.txt .
RUN pip install --upgrade -r requirements.txt

WORKDIR /app
COPY app/*.py .

#ENTRYPOINT [ "streamlit", "run", "app.py"]
#ENTRYPOINT [ "python", "-m", "http.server", "8000"]
