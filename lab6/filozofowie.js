// Teoria Współbieżnośi, implementacja problemu 5 filozofów w node.js
// Opis problemu: http://en.wikipedia.org/wiki/Dining_philosophers_problem
//   https://pl.wikipedia.org/wiki/Problem_ucztuj%C4%85cych_filozof%C3%B3w
//+ 1. Dokończ implementację funkcji podnoszenia widelca (Fork.acquire).
//+ 2. Zaimplementuj "naiwny" algorytm (każdy filozof podnosi najpierw lewy, potem
//    prawy widelec, itd.).
//+ 3. Zaimplementuj rozwiązanie asymetryczne: filozofowie z nieparzystym numerem
//    najpierw podnoszą widelec lewy, z parzystym -- prawy.
//+ 4. Zaimplementuj rozwiązanie z kelnerem (według polskiej wersji strony)
//+ 5. Zaimplementuj rozwiązanie z jednoczesnym podnoszeniem widelców:
//    filozof albo podnosi jednocześnie oba widelce, albo żadnego.
// 6. Uruchom eksperymenty dla różnej liczby filozofów i dla każdego wariantu
//    implementacji zmierz średni czas oczekiwania każdego filozofa na dostęp
//    do widelców. Wyniki przedstaw na wykresach.

var async = require("async");
const fs = require('fs');

const {
    performance,
    PerformanceObserver
} = require('perf_hooks');

var eatTime = 10;

var Fork = function() {
    this.state = 0;
    return this;
};

Fork.prototype.acquire = function(cb) {
    this.acquireBEB(cb, 1);
};

Fork.prototype.acquireBEB = function(cb, waitTime) {
    var self = this;
    setTimeout(function () {
        if (self.state === 0) {
            self.state = 1;
            if(cb) cb();
        } else {
            self.acquireBEB(cb, waitTime * 2);
        }
    }, waitTime);
};

Fork.prototype.release = function(cb) {
    this.state = 0;
    if(cb) cb();
};

var Philosopher = function(id, forks) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.f2 = (id+1) % forks.length;
    return this;
};

Philosopher.prototype.startNaive = function(count, cb) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        self = this;

    if (count > 0) {
        var startWaitTime = performance.now();
        forks[f1].acquire(function () {
            // console.log(id + ": Acquired " + f1);
            forks[f2].acquire(function() {
                sumTime += performance.now() - startWaitTime;
                // console.log(id + ": Acquired " + f2);
                setTimeout(function() {
                    // console.log(id + ": Eating");
                    async.waterfall([
                        function (cb) {
                            // console.log(id + ": Releasing " + f1);
                            forks[f1].release(cb);
                        },
                        function (cb) {
                            // console.log(id + ": Releasing " + f2);
                            forks[f2].release(cb);
                        },
                        function () {
                            self.startNaive(count - 1, cb);
                        }
                    ])
                }, eatTime)
            })
        })
    } else {
        // console.log(id + " - FINISH");
        if (cb) cb();
    }
};

Philosopher.prototype.startAsym = function(count, cb) {
    var f1 = this.f1,
        f2 = this.f2;

    if (this.id % 2 === 0) {
        this.f2 = [f1, this.f1 = f2][0];
    }
    this.startNaive(count, cb);
};

var Conductor = function() {
    this.queue = [];
    return this;
};

Conductor.prototype.ask = function (philosopher, cb) {
    var f1 = philosopher.f1,
        f2 = philosopher.f2,
        forks = philosopher.forks;

    if (forks[f1].state === 0 && forks[f2].state === 0) {
        // allow philosopher to eat
        // console.log(philosopher.id + ": Allowed to eat.");
        forks[f1].state = 1;
        forks[f2].state = 1;
        if (cb) cb();
    } else {
        // console.log(philosopher.id + ": I have to wait.");
        this.queue.push([philosopher, cb]);
    }
};

Conductor.prototype.inform = function(philosopher) {
    var f1 = philosopher.f1,
        f2 = philosopher.f2,
        forks = philosopher.forks,
        queue = this.queue;

    forks[f1].state = 0;
    forks[f2].state = 0;

    var clearQueue = function () {
        if (queue.length > 0) {
            var nextPhilosopher = queue[0][0];
            var callback = queue[0][1];
            f1 = nextPhilosopher.f1;
            f2 = nextPhilosopher.f2;

            if (forks[f1].state === 0 && forks[f2].state === 0) {
                // allow philosopher to eat
                // console.log(philosopher.id + ": Allowed to eat after waiting.");
                queue.shift();
                forks[f1].state = 1;
                forks[f2].state = 1;
                if (callback) callback();
                clearQueue();
            }
        }
    };
    clearQueue();
};

Philosopher.prototype.startConductor = function(count, cb) {
    var id = this.id,
        self = this;
    if (count > 0) {
        // console.log(id + ": Asking for permission to eat.");
        var startWaitTime = performance.now();
        conductor.ask(this, function () {
            sumTime += performance.now() - startWaitTime;
            setTimeout(function () {
                conductor.inform(self);
                self.startConductor(count - 1, cb);
            }, eatTime);
        })
    } else {
        // console.log(id + " - FINISH");
        if (cb) cb();
    }
};


// TODO: wersja z jednoczesnym podnoszeniem widelców
// Algorytm BEB powinien obejmować podnoszenie obu widelców,
// a nie każdego z osobna

Philosopher.prototype.acquireTwoForks = function(cb) {
    this.acquireTwoForksBEB(cb, 1);
};

Philosopher.prototype.acquireTwoForksBEB = function(cb, waitTime) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        self = this;

    setTimeout(function () {
        if (forks[f1].state === 0 && forks[f2].state === 0) {
            // console.log(id + ": acquired my two forks.");
            forks[f1].state = 1;
            forks[f2].state = 1;
            if(cb) cb();
        } else {
            self.acquireTwoForksBEB(cb, waitTime * 2);
        }
    }, waitTime);
};

Philosopher.prototype.releaseTwoForks = function() {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;

    // console.log(id + ": releasing my two forks.");
    forks[f1].state = 0;
    forks[f2].state = 0;
};

Philosopher.prototype.startTakeTwoForks = function (count, cb) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        self = this;

    if (count > 0) {
        var startWaitTime = performance.now();
        self.acquireTwoForks(function () {
            sumTime += performance.now() - startWaitTime;
            setTimeout(function () {
                self.releaseTwoForks();
                self.startTakeTwoForks(count - 1, cb);
            }, eatTime)
        })

    } else {
        // console.log(id + " - FINISH");
        if (cb) cb();
    }
};

var N = 5;
var p = 10;
var conductor = new Conductor();
var sumTime = 0;
var step = 5;
var maxPhilosophersNum = 100;


/** NAIVE */
// for (var i = 0; i < N; i++) {
//     philosophers[i].startNaive(p);
// }

function run(number, filename, cb) {
    if (number < maxPhilosophersNum) {
        sumTime = 0;
        var counter = number;
        var forks = [];
        var philosophers = [];

        for (var i = 0; i < number; i++) {
            forks.push(new Fork());
        }

        for (var i = 0; i < number; i++) {
            philosophers.push(new Philosopher(i, forks));
        }

        function callb() {
            counter--;
            // console.log(number + " " + counter);
            if (counter === 0) {
                fs.appendFileSync(filename, number + ',' + sumTime/(number*p) + '\n');
                run(number + step, filename, cb);
            }
        }

        if (filename === "waiter") {
            conductor = new Conductor();
            /** WAITER */
            for (var i = 0; i < number; i++) {
                philosophers[i].startConductor(p, callb);
            }
        } else if (filename === "asym") {
            /** ASYM */
            for (var i = 0; i < number; i++) {
                philosophers[i].startAsym(p, callb);
            }
        } else if (filename === "twoforks") {
            /** TWO FORKS */
            for (var i = 0; i < number; i++) {
                philosophers[i].startTakeTwoForks(p, callb);
            }
        }
    } else {
        if(cb) cb();
    }
}
function f() {
    async.waterfall([
        function (cb) {
            console.log("ASYM");
            run(N, "asym", cb);
        }, function (cb) {
            console.log("WAITER");
            run(N, "waiter", cb);
        },
        function (cb) {
            console.log("TWO FORKS");
            run(N, "twoforks", cb);
        }
    ]);
}

f();

