from graphviz import Digraph

dot = Digraph(format='png')


class Node:
    def __init__(self, i, label):
        self.i = i
        self.label = label

    def __repr__(self):
        return str(self.i) + ': ' + str(self.label)

    def __eq__(self, other):
        return self.i == other.i and self.label == other.label


class Graph:
    def __init__(self):
        self.nodes = []
        self.edges = []


def create_graphviz_graph(a, H):
    for node in H.nodes:
        dot.node(*node)
    dot.edges(list(map(lambda edge: str(edge[0].i) + str(edge[1].i), H.edges)))

    print("\nGraf w formacie dot:")
    print(dot.source)
    dot.render(''.join(a) + '.gv', view=True)


def foata_from_hasse(H):
    FNF = ''
    current_nodes = []

    while True:
        current_nodes.clear()
        for node in H.nodes:
            if all(edge[1] != Node(*node) for edge in H.edges):
                current_nodes.append(node)

        if current_nodes:
            FNF += '('
            current_nodes.sort(key=lambda n: n[1])
            for node in current_nodes:
                FNF += Node(*node).label
                H.nodes = list(filter(lambda x: node != x, H.nodes))
                H.edges = list(filter(lambda edge: edge[0] != Node(*node), H.edges))

            FNF += ')'
        if not current_nodes:
            break

    print("\nFNF na podstawie grafu: " + FNF)


def get_hasse_graph(a, D):
    H = Graph()
    MIN = []

    for i in range(len(a)-1, -1, -1):
        new_node = Node(str(i), a[i])
        MIN.append(new_node)
        H.nodes.append((str(i), a[i]))
        for x in MIN:
            if x != new_node:
                if (new_node.label, x.label) in D:
                    H.edges.append((new_node, x))

        for edge1 in H.edges:
            for edge2 in H.edges:
                if edge2 != edge1:
                    n1, n2 = edge1
                    n3, n4 = edge2
                    if n2 == n3 and (n1, n4) in H.edges:
                        H.edges.remove((n1, n4))

                    if n1 == n4 and (n3, n2) in H.edges:
                        H.edges.remove((n3, n2))

    # print(H.edges)
    create_graphviz_graph(a, H)
    foata_from_hasse(H)

