import matplotlib.pyplot as plt
import numpy as np

def plot_from_file(file):
    with open(file, 'r') as f:
        content = f.readlines()
        x = [int(tuple(z.strip().split(','))[0]) for z in content]
        y = [round(float(tuple(z.strip().split(','))[1]), 2) for z in content]
        # print(x)
        # print(y)
        if file == "asym":
            plt.plot(x, y,  marker='o', markerfacecolor='blue', markersize=2, color='blue', label="Asym")
        elif file == "waiter":
            plt.plot(x, y,  marker='o', markerfacecolor='green', markersize=2, color='green',
                     linestyle='dashed', label="Waiter")
        else:
            plt.plot(x, y,  marker='o', markerfacecolor='red', markersize=2, color='red', label="Both forks")


def main():
    filenames = ["asym", "waiter", "twoforks"]
    for file in filenames:
        plot_from_file(file)

    plt.legend()
    plt.savefig("results.png")


if __name__ == '__main__':
    main()