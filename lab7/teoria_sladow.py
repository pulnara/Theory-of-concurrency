import sys
import itertools
import graf


def all_stacks_are_empty(stacks_dict):
    for stack in stacks_dict.values():
        if len(stack) > 0:
            return False
    return True


def get_with_letter_on_top(stacks_dict):
    with_letter_on_top = []
    for letter in stacks_dict.keys():
        if len(stacks_dict[letter]) > 0 and stacks_dict[letter][-1] != '*':
            with_letter_on_top.append(letter)
    return with_letter_on_top


def get_FNF(A, w, D):
    stacks_dict = {}
    for letter in A:
        stacks_dict[letter] = []

    for letter in w[::-1]:
        stacks_dict[letter].append(letter)
        for dependency in D:
            if dependency[0] == letter and dependency[0] != dependency[1]:
                stacks_dict[dependency[1]].append('*')

    FNF = ''

    while True:
        if all_stacks_are_empty(stacks_dict):
            break
        FNF += '('
        stacks_with_letter_on_top = get_with_letter_on_top(stacks_dict)
        chars_to_add = []
        for letter in stacks_with_letter_on_top:
            stack = stacks_dict[letter]
            char = stack.pop()
            chars_to_add.append(char)
            for dep in list(filter(lambda tup: tup[0] == char and tup[1] != char, D)):
                to = dep[1]
                if stacks_dict[to][-1] == '*':
                    stacks_dict[to].pop()
        chars_to_add.sort()
        FNF += ''.join(chars_to_add)
        FNF += ')'

    print("\nFNF[w] = " + FNF)


def main():
    if len(sys.argv) < 3:
        exit(2)

    A = sys.argv[1].replace('{', '').replace('}', '').split(',')
    I = sys.argv[2].replace('{', '').replace('}', '').replace(',(', ' (').split()
    I = [tuple(x.replace('(', '').replace(')', '').split(',')) for x in I]
    E2 = list(itertools.product(A, A))
    D = list(filter(lambda x: x not in I, E2))

    w = sys.argv[3]

    print("A = ", A)
    print("w = ", w)
    print("I = ", I)
    print()
    print("D = ", D)

    get_FNF(A, w, D)

    graf.get_hasse_graph(list(w), D)


if __name__ == "__main__":
    main()
