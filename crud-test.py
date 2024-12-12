import requests

# Base URL API
BASE_URL = "http://34.101.41.219:3000/diaries"

# Header dengan token JWT
HEADERS = {
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJMQjRBRklYMnZ3bEsxZVBEeFBpQSIsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsImV4cCI6MTczMzg3MDU0OCwiaWF0IjoxNzMzODY2OTQ4fQ.6ci4mf3Od-Xwgl3fnWKFTEnYfCrYHrQ93woz3Gqb5Ns",
    "Content-Type": "application/json"
}

def test_create_diary():
    data = {
        "userId": "LB4AFIX2vwlK1ePDxPiA",  # Ganti dengan userId yang sesuai
        "date": "2024-12-09",
        "content": "Pada hari ini kuturut ayah ke kota, naik delman kududuk di muka"
    }
    response = requests.post(BASE_URL, json=data, headers=HEADERS)
    print("Create Diary Response:", response.status_code, response.json())
    return response.json().get("diaryId")

def test_get_diaries(user_id):
    params = {"userId": user_id}
    response = requests.get(BASE_URL, params=params, headers=HEADERS)
    print("Get Diaries Response:", response.status_code, response.json())

#def test_update_diary(diary_id):
#    data = {
#        "date": "2024-12-11",
#        "content": "Hari ini saya merasa sangat bersemangat untuk belajar lebih banyak."
#    }
#    response = requests.put(f"{BASE_URL}/{diary_id}", json=data, headers=HEADERS)
#    print("Update Diary Response:", response.status_code, response.json())


if __name__ == "__main__":
    # Step 1: Create a new diary
    diary_id = test_create_diary()

    if diary_id:
        # Step 2: Fetch all diaries for the user
        test_get_diaries("LB4AFIX2vwlK1ePDxPiA")

        # Step 3: Update the created diary
       # test_update_diary(diary_id)

        # Step 4: Delete the created diary
        #test_delete_diary(diary_id)
