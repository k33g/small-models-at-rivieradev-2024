FROM langchain/langchain

WORKDIR /app
RUN <<EOF
apt-get update
apt-get install -y build-essential curl software-properties-common
apt-get install -y mc nano
rm -rf /var/lib/apt/lists/*
EOF

# ------------------------------------
# Install OhMyBash
# ------------------------------------
RUN <<EOF
bash -c "$(curl -fsSL https://raw.githubusercontent.com/ohmybash/oh-my-bash/master/tools/install.sh)"
EOF
    
WORKDIR /dependencies
COPY requirements.txt .
RUN pip install --upgrade -r requirements.txt

WORKDIR /app
COPY app/*.py .

#EXPOSE 8501
ENTRYPOINT [ "streamlit", "run", "app.py"]
#ENTRYPOINT [ "python", "-m", "http.server", "8000"]
