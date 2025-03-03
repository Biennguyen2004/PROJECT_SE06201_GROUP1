from flask import Flask

app = Flask(__name__)

@app.route('/about')
def about():
    return "Giới thiệu"

if __name__ == '__main__':
    app.run(debug=True)
