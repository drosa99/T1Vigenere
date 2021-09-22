/**
 * Autora: Bianca Camargo
 * Trabalho 1 de Segurança de Sistemas
 */

const fs = require('fs');
const readFile = require('./utils/readFile');

const args = process.argv.slice(2);

const freqLetterToUse = 
  args[0] && args[0].length == 1 ? args[0] 
  : (args[1] && args[1].length == 1 ? args[1] : 'a');

const letters = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'];
// índex de coincidência da língua portuguesa. Letras mais frequentes: a, e, l
const icPT = 0.072723;
let ciphertext;
let IC = 0;


const main = () => {
  ciphertext = readFile();

  if (ciphertext) {
    let keyLength = 1;
    // exibe as tentativas para o tamanho do alfabeto
    for (let i = keyLength; i <= letters.length; i++) {
      let substrings = new Array();

      getSubstrings(i, substrings);

      console.log("- Tamanho da chave: ", i);

      // calcula ICs para todas as substrings
      substrings.map((string, index) => {
        const substringIC = getIC(string);
        console.log("  ", substringIC.toFixed(6), " ==> ", index);
        console.log("  substringIC-icPT: ", substringIC-icPT, " ==> ", index);
        const localDiff = substringIC-icPT;
        if (localDiff <= 0.009 && localDiff > 0) {
          keyLength = i;
          return;
        }
      });

      if(keyLength != 1) break;
    }

    let finalSubstring = new Array();
    if (keyLength == 1) {
      console.log("Tamanho da chave não encontrada com diferença de até 0.009");
    }

    getSubstrings(keyLength, finalSubstring);

    let freqLetters = new Array();
    let letterDiff = new Array();

    let completeKey = '';

    for (let j = 0; j < finalSubstring.length; j++) {
      freqLetters.push(getFreqLetter(finalSubstring[j]));
      let diff = letters.indexOf(freqLetters[j]) - letters.indexOf(freqLetterToUse || 'a');
      if (diff < 0) {
        diff += 26
      }
      letterDiff.push(diff);
      completeKey = completeKey + letters[diff];
    }
    
    console.log('Letras mais frequentes das substrings: ', freqLetters);
    console.log('Deslocamentos por substring:', letterDiff);
    console.log('Chave encontrada:', completeKey);

    writeFinalFile(completeKey);
  }
}

// retorna a letra mais frequente da string
const getFreqLetter = (string) => {
  let mostCommon = '';
  let higherOccurrence = -1;

  letters.forEach(letter => {
    const occurrence = (string.match(new RegExp(letter, "g"))||[]).length;
    if (occurrence > higherOccurrence) {
      higherOccurrence = occurrence;
      mostCommon = letter;
    }
  });
  return mostCommon;
}

// separa em arrays de string de acordo com o suposto tamanho da key
const getSubstrings = (keyLength, substringsArray) => {
  [...ciphertext].forEach((l, index) => {
    const position = index % keyLength;
    let stringToConcat = substringsArray[position];
    if (stringToConcat)
      substringsArray.splice(position, 1, `${stringToConcat}${l}`);
    else {
      substringsArray.splice(position, 1, l);
    }
  })
};

// retorna o índice de coincidência de determinada string
const getIC = (stringTarget) => {
  let length = 0;
  let fi = 0;

  letters.forEach(letter => {
    const occurrence = (stringTarget.match(new RegExp(letter, "g"))||[]).length;

    // 0-25 fi(fi - 1)
    if (occurrence > 0) {
      length += occurrence;
      fi += occurrence * (occurrence - 1);
    }
  });

  // n * (n - 1)
  const n = length * (length - 1);

  IC =  fi / n;

  return IC;
}

// escreve o resultado da decodificação no arquivo output.txt
const writeFinalFile = (completeKey) => {
  let decryptedText = '';
  let keyIndex = 0;
  [...ciphertext].forEach((l) => {
    if(keyIndex == completeKey.length) keyIndex = 0;
    
    let decryptedLetter = letters.indexOf(l) - letters.indexOf(completeKey[keyIndex]);

    if (decryptedLetter < 0) {
      decryptedLetter = decryptedLetter + letters.length;
    }
    decryptedText = decryptedText + letters[decryptedLetter];
    keyIndex++;
  });

  fs.writeFile('output.txt', decryptedText, function (err) {
    if (err) return console.log(err);
    console.log('\nVeja o arquivo output.txt');
  });
}

main();
