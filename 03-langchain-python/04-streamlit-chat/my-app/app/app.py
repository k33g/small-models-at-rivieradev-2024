import os
from langchain_community.llms import ollama
from langchain_community.callbacks import StreamlitCallbackHandler
from langchain.prompts import PromptTemplate

# All we need for a good chat
from langchain.memory import ConversationBufferMemory
from langchain.chains import ConversationChain

import streamlit as st

ollama_base_url = os.getenv("OLLAMA_BASE_URL")
model_name = os.getenv("LLM")

memory = ConversationBufferMemory()
## session state variable
if 'chat_history' not in st.session_state:
    st.session_state.chat_history=[]
else:
    for message in st.session_state.chat_history:
        memory.save_context({'input': message['human']}, {'output': message['AI']})

prompt_template = PromptTemplate(
    input_variables=['history', 'input'],
    template="""
    You are a technical writer, specialist with the rustlang programming languages, 
    you will write an answer to the question for the noobs,
    with some source code examples.
    {history}

    Human: {input}
    AI:
    """
) 

model = ollama.Ollama(
    temperature=0,
    repeat_penalty=1,
    base_url=ollama_base_url, 
    model=model_name,
)

conversation_chain = ConversationChain(
    prompt=prompt_template,
    llm=model,
    memory=memory,
    verbose=True, # then you can see the intermediate messages
)

# Add a title and a subtitle to the webapp
st.title("🤖 I'm Pi-Lot")
st.header("I 💙 programming with 🦀 Rust")
st.header("👋 I'm running on a PI5")

# Text input fields
user_input = st.chat_input("Topic:")

# Executing the chain when the user 
# has entered a topic  
if user_input:
    st_callback = StreamlitCallbackHandler(st.container())

    result = conversation_chain.invoke(
        {"input":user_input, "history":st.session_state["chat_history"]}, 
        {"callbacks":[st_callback]}
    )

    message = {'human': user_input, 'AI': result["response"]}
    st.session_state.chat_history.append(message)
    
    with st.expander(label='Chat history', expanded=False):
        st.write(st.session_state.chat_history)
