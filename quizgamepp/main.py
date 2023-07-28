from pywebio.input import *
from pywebio.output import *
from pymongo import MongoClient
import time

client = MongoClient('localhost', port=27017)
db = client.quiz_db
coll = db.users

def front():
    sel = radio('Choose what to do?', ['LOGIN', 'REGISTER'])
    if sel == 'LOGIN':
        login()
    else:
        register()

def homescreen(data):
    sel1 = radio('Would you like to go to Quiz page', ['Yes', 'No'])
    if sel1 == 'Yes':
        return quiz_game(data)
    else:
        return front()

def login():
    data = input_group("LOGIN",
                       [input('USERNAME', name='Name', type=TEXT, required=True, help_text='Please enter your username'),
                        input('USER ID', name='Id', type=PASSWORD, required=True),
                        input('PASSWORD', name='PASS', type=PASSWORD, required=True,
                              help_text='Please enter your password')])

    sel1 = radio('Would you like to go to Quiz page', ['Yes', 'No'])
    if sel1 == 'Yes':
        return quiz_game(data)
    else:
        return front()

def quiz_game(data):
    questions = [
        {
            'question': "Who developed Python Programming Language?",
            'options': ['Wick van Rossum', 'Rasmus Lerdorf', 'Guido van Rossum', 'Niene Stom'],
            'answer': 'Guido van Rossum'
        },
        {
            'question': "Which type of Programming does Python support?",
            'options': ['object-oriented programming', 'structured programming', 'functional programming', 'ALL of these'],
            'answer': 'ALL of these'
        },
        {
            'question': "Is Python case sensitive when dealing with identifiers?",
            'options': ['no', 'yes', 'machine dependent', 'none'],
            'answer': 'yes'
        },
        {
            'question': "Which of the following is the correct extension of the Python file?",
            'options': ['.py', '.pl', '.p', '.pyth'],
            'answer': '.py'
        }
    ]

    answers = {}
    for q in questions:
        selection = radio(q['question'], q['options'], required=True, help_text='You have 10 seconds to answer each question.')
        answers[q['question']] = selection

    coll.insert_one({'Name': data['Name'], 'USER Id': data['Id'], **answers})
    put_text('Your response has been recorded')

    again = radio('Do another quiz', ['Yes', 'No'])
    if again == 'Yes':
        front()
    else:
        score = calculate_score(answers)
        result(score)

def calculate_score(answers):
    correct_answers = {
        "Who developed Python Programming Language?": "Guido van Rossum",
        "Which type of Programming does Python support?": "ALL of these",
        "Is Python case sensitive when dealing with identifiers?": "yes",
        "Which of the following is the correct extension of the Python file?": ".py"
    }

    score = 0
    for question, user_answer in answers.items():
        correct_answer = correct_answers.get(question)
        if user_answer == correct_answer:
            score += 1

    return score

def result(score):
    put_text('Your Quiz Result')
    put_text(f'Your Score: {score}')
    put_text('Thank you for playing!')

def register():
    data = input_group("REGISTRATION",
                       [input('Enter your name', name='Name', type=TEXT),
                        input('Enter your Age', name='Age', type=NUMBER),
                        input('Enter your user ID', name='Id', type=NUMBER, required=True),
                        input('Enter your Password', name='Pass', type=PASSWORD, required=True)])

    if data['Age'] >= 10:
        put_text('Check your Detail')
        put_table([['NAME', put_text(data['Name'])],
                   ['AGE', put_text(data['Age'])],
                   ['ID', put_text(data['Id'])],
                   ['PASSWORD', put_text(data['Pass'])]])

        check = checkbox(options=['All details are Correct'])
        if check:
            put_text('You are successfully Registered')
            users = {
                'Name': data['Name'],
                'USER Id': data['Id'],
                'AGE': data['Age'],
                'PASSWORD': data['Pass']
            }
            coll.insert_one(users)

            sel2 = radio('Would you like to go to Quiz Page', ['Yes', 'No'])
            if sel2 == 'Yes':
                login()
            else:
                front()
    else:
        put_text('You are not eligible for the Quiz')
def quiz():
    put_text('Welcome to Quiz Game')
    image_path = 'img/qg.png'
    put_image(open(image_path, 'rb').read())

if __name__ == '__main__':
    quiz()
    front()
