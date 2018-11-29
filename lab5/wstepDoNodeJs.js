function printAsync(s, cb) {
    var delay = Math.floor((Math.random()*1000)+500);
    setTimeout(function() { // emulujemy jakies obliczenia
        console.log(s);
        if (cb) cb();       // jak sie zakonczy,to jest wywolywany anonimowy print
    }, delay);
}

printAsync("1");
printAsync("2");
printAsync("3");

console.log('done!');