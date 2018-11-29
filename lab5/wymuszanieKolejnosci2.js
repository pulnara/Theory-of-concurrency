function printAsync(s, cb) {
    var delay = Math.floor((Math.random()*1000)+500);
    setTimeout(function() {
        console.log(s);
        if (cb) cb();
    }, delay);
}

function task1(cb) {
    printAsync("1", function() {
        task2(cb);
    });
}

function task2(cb) {
    printAsync("2", function() {
        task3(cb);
    });
}

function task3(cb) {
    printAsync("3", cb);    // do czego odwoluje sie parametr cb?
}

// wywolanie sekwencji zadan
// task1(function() {
//     console.log('done!');
// });
//

/*
** Zadanie:
** Napisz funkcje loop(n), ktora powoduje wykonanie powyzszej
** sekwencji zadan n razy.
**
*/
function loop(n) {
    if (n>0) {
        task1(function() {
            loop(n-1); // wywolywany przez callback
        })
    } else {
        console.log('done!')
    }
}


loop(4);