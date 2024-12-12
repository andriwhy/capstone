import google.generativeai as genai

genai.configure(api_key="AIzaSyAO16cRX-IT0HmaUHcZrm4MXMkF4m1xdeY")
model = genai.GenerativeModel("gemini-1.5-flash")
response = model.generate_content("Explain how AI works")
print(response.text)