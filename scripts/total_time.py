def is_integer_all(ss):
    for s in ss:
        if not s.isdigit():
            return False

    return True

def is_valid(input_list):
    ''' 
        데이터 확인
            -> 시간 분 (시간 - 띄어쓰기 - 분 인가)
            -> 모두 int 인지
            -> 분 < 60
    '''
    if len(input_list) != 2: return False
    if not is_integer_all(input_list): return False
    if int(input_list[1]) >= 60: return False

    return True

str_table = []
time_table = []

while True:
    raw = input("A장르 시간 입력 (예시 0시간 20분 -> 0 20, 종료는 그냥 Enter): ")
    
    # end
    if raw == "":
        break
    
    # check valid
    divid_list = raw.split()
    if not is_valid(divid_list):
        print("예시에 맞게 다시 입력해 주세요! (정수 입력, 시간 분, 분 < 60)")
        continue
    
    # add text
    str_table.append(f"{divid_list[0]}시간 {divid_list[1]}분")

    # add time
    minute = int(divid_list[0]) * 60 + int(divid_list[1])
    time_table.append(minute)

# calc
total_t = sum(time_table)
print(f"=======( total: {total_t//60}시간 {total_t%60}분 )===========")
for i in range(len(time_table)):
    time_table[i] = time_table[i] / total_t
    print(f"Type{i}: {str_table[i]} [{time_table[i] * 100:.1f}%]")

'''
장르를 추가할 수 있게 기능 추가하면 안되나?
일
공부 - 독서, 자율주행&AI, 
운동
'''