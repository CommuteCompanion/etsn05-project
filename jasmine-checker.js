const puppeteer = require('puppeteer');

(async () => {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    await page.goto('http://localhost:8080/specs.html', {waitUntil: 'networkidle2'});

    let texts = await page.evaluate(() => {
        let data = [];
        let elements = document.getElementsByClassName('jasmine-failed');
        for (var element of elements)
            data.push(element.textContent);
        return data;
    });

    if (texts.length >= 1) {
        texts.forEach(element => {
            console.log(element);
        });
        return process.exit(1);
    }
    return process.exit(0);
})();
