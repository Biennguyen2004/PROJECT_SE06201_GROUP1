from maix import nn, camera, display, image, time, touchscreen, app
import math
import os
from maix import gpio, pinmap, time

pinmap.set_pin_function("A18", "GPIOA18")
lock = gpio.GPIO("GPIOA18", gpio.Mode.OUT)
lock.value(0)


pressed_flag = [False, False, False]
learn_id = 0

def main(disp):
    global pressed_flag, learn_id
    img = image.Image(disp.width(), disp.height())
    msg = "loading ..."
    size = image.string_size(msg, scale=2, thickness=2)
    img.draw_string((img.width() - size.width()) // 2, (img.height() - size.height()) // 2, msg, color=image.COLOR_WHITE, scale=2, thickness=2)
    disp.show(img)

    recognizer = nn.FaceRecognizer(detect_model="/root/models/retinaface.mud", feature_model="/root/models/face_feature.mud", dual_buff=True)

    if os.path.exists("/root/faces.bin"):
        recognizer.load_faces("/root/faces.bin")

    cam = camera.Camera(recognizer.input_width(), recognizer.input_height(), recognizer.input_format())
    ts = touchscreen.TouchScreen()

    while not app.need_exit():
        img = cam.read()
        faces = recognizer.recognize(img, 0.5, 0.45, 0.85, False, False)
        
        for obj in faces:
            name = recognizer.labels[obj.class_id] if obj.class_id < len(recognizer.labels) else "unknown"
            
            if name == "Tung": 
                lock.value(1)
                #time.sleep_ms(5000)
            else: 
                lock.value(0)
            #time.sleep_ms(500)
            color = image.COLOR_GREEN if name == "Tung" else image.COLOR_RED
            img.draw_rect(obj.x, obj.y, obj.w, obj.h, color=color)
            radius = math.ceil(obj.w / 10)
            img.draw_keypoints(obj.points, color, size=radius if radius < 5 else 4)
            msg = f'{name}: {obj.score:.2f}'
            img.draw_string(obj.x, obj.y - 10, msg, color=color)
        
        disp.show(img)

disp = display.Display()
try:
    main(disp)
except Exception:
    import traceback
    msg = traceback.format_exc()
    img = image.Image(disp.width(), disp.height())
    img.draw_string(0, 0, msg, image.COLOR_WHITE)
    disp.show(img)
    while not app.need_exit():
        time.sleep_ms(100)