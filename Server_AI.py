# Server_AI.py
import face_recognition
import os
import cv2
import numpy as np
import pickle
from flask import Flask, jsonify, Response

# Đường dẫn thư mục chính chứa dataset
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
                    print(f"Đã mã hóa ảnh: {filename} của {person_folder}")
                else:
                    print(f"Không tìm thấy khuôn mặt trong ảnh: {filename} của {person_folder}")

    with open(data_file, 'wb') as f:
        pickle.dump({'known_faces': known_faces, 'known_names': known_names}, f)
    print(f"Đã lưu dữ liệu vào: {data_file}")
    return known_faces, known_names


# Hàm tải dữ liệu đã lưu
def load_data():
    if os.path.exists(data_file):
        with open(data_file, 'rb') as f:
            data = pickle.load(f)
        print(f"Đã tải dữ liệu từ: {data_file}")
        return data['known_faces'], data['known_names']
    else:
        print("Không tìm thấy file dữ liệu, đang huấn luyện mới...")
        return train_and_save()


# Tải hoặc huấn luyện dữ liệu
known_faces, known_names = load_data()

# Tạo Flask server
app = Flask(__name__)


@app.route('/detect', methods=['GET'])
def detect_face():
    # Khởi động webcam
    video_capture = cv2.VideoCapture(0)
    ret, frame = video_capture.read()
    if not ret:
        video_capture.release()
        return jsonify({"error": "Không thể truy cập webcam"}), 500

    # Thu nhỏ khung hình để tăng tốc độ
    small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
    rgb_small_frame = np.ascontiguousarray(small_frame[:, :, ::-1])

    # Tìm vị trí khuôn mặt và mã hóa
    face_locations = face_recognition.face_locations(rgb_small_frame)
    face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

    face_names = []
    for face_encoding in face_encodings:
        face_distances = face_recognition.face_distance(known_faces, face_encoding)
        matches = face_recognition.compare_faces(known_faces, face_encoding, tolerance=0.5)

        name = "Unknown"
        if len(face_distances) > 0:
            best_match_index = np.argmin(face_distances)
            if matches[best_match_index] and face_distances[best_match_index] < 0.5:
                name = known_names[best_match_index]
        face_names.append(name)

    # Hiển thị trên webcam (tùy chọn)
    for (top, right, bottom, left), name in zip(face_locations, face_names):
        top *= 4
        right *= 4
        bottom *= 4
        left *= 4
        if name == "Unknown":
            cv2.rectangle(frame, (left, top), (right, bottom), (0, 0, 255), 2)
        else:
            cv2.rectangle(frame, (left, top), (right, bottom), (0, 255, 0), 2)
        cv2.putText(frame, name, (left, top - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)

    cv2.imshow('Video', frame)
    cv2.waitKey(1)

    # Giải phóng webcam
    video_capture.release()

    # Trả về dữ liệu JSON cho server khác
    return jsonify({"faces": face_names})


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, threaded=True)