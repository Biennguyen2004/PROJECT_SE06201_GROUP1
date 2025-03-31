import os

from keras.models import load_model
import numpy as np
import cv2

# Tải mô hình nhận diện khuôn mặt có sẵn
model = load_model("mobilefacenet.h5")

# Hàm trích xuất đặc trưng khuôn mặt
def extract_face_embedding(image_path):
    img = cv2.imread(image_path)
    img = cv2.resize(img, (112, 112))  # Resize phù hợp với mô hình
    img = np.expand_dims(img, axis=0) / 255.0  # Chuẩn hóa dữ liệu
    embedding = model.predict(img)
    return embedding

# Lưu embeddings vào file để sử dụng trên MaixCAM
dataset = "C:/Users/TungHoi/Desktop/Training AI/dataset"
known_faces = {}
for person in os.listdir(dataset):
    person_path = os.path.join(dataset, person)
    if os.path.isdir(person_path):
        for filename in os.listdir(person_path):
            image_path = os.path.join(person_path, filename)
            known_faces[person] = extract_face_embedding(image_path)

np.save("face_embeddings.npy", known_faces)
print("Lưu embeddings xong!")
