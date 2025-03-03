# Training_AI.py
import face_recognition
import os
import pickle

# data set and file path for save data
dataset_folder = "C:/Users/TungHoi/Desktop/Training AI/dataset"
data_file = "C:/Users/TungHoi/Desktop/Training AI/known_faces.pkl"

# Hàm huấn luyện và lưu dữ liệu
def train_and_save():
    known_faces = []
    known_names = []

    for person_folder in os.listdir(dataset_folder):
        person_path = os.path.join(dataset_folder, person_folder)
        if os.path.isdir(person_path):
            for filename in os.listdir(person_path):
                image_path = os.path.join(person_path, filename)
                image = face_recognition.load_image_file(image_path)
                face_encodings = face_recognition.face_encodings(image)
                if len(face_encodings) > 0:
                    encoding = face_encodings[0]
                    known_faces.append(encoding)
                    known_names.append(person_folder)
                    print(f"Encryption: {filename} của {person_folder}")
                else:
                    print(f"do not find face in picture: {filename} of {person_folder}")

    with open(data_file, 'wb') as f:
        pickle.dump({'known_faces': known_faces, 'known_names': known_names}, f)
    print(f"Đã lưu dữ liệu vào: {data_file}")
    return known_faces, known_names

if __name__ == "__main__":
    train_and_save()