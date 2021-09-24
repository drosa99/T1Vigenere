package br.drosa99;

import java.io.*;
import java.util.*;

public class Vigenere {

    private static String[] letras = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    //private static HashMap<String, Integer> letrasMap = (HashMap<String, Integer>) Map.ofEntries(Map.entry("a", 0), Map.entry("b", 1));
    private static HashMap<String, Integer> letrasMap = new HashMap<>();

    private static double icPt = 0.072723;
    private static long ic = 0;
    //private static List<String> textoCifrado = Arrays.asList("a", "t", "a", "c", "a", "r", "a", "o");
//    private static List<String> textoCifrado = Arrays.asList("l", "b", "l", "k", "l", "z", "l", "w");
    private static String[] textoCifrado;

    //TODO fazer suporte para ingles

    public static void main(String[] args) {

        for (int i = 0; i < letras.length; i++) {
            letrasMap.put(letras[i], i);
        }

        long inicio = System.currentTimeMillis();
        try {
            leituraArquivo();
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo do texto criptografado.");
            System.exit(0);
        }

//        String[] b = new String[textoCifrado.size()];
//        String[] c = new String[textoCifrado.size()];
//        int index = 0;
//
//        long inicio = System.currentTimeMillis();
//        for (String l: textoCifrado) {
//            b[index] = l;
//            index++;
//        }
//        long fim = System.currentTimeMillis();
//        System.out.println("List -> Tempo em milis " + (fim - inicio));
//
//        inicio = System.currentTimeMillis();
//        for (int i = 0; i < b.length; i++) {
//            c[i] = b[i];
//        }
//        fim = System.currentTimeMillis();
//        System.out.println("Array -> Tempo em milis " + (fim - inicio));


        String chave = encontrarChave();
        String textoDescriptografado = descriptografar(chave);
        System.out.println(" O texto foi descriptografado com sucesso");
        try {
            escreverTexto(textoDescriptografado);
        } catch (IOException e) {
            System.out.println(" Erro ao escrever arquivo com o texto descriptografado.");
            System.exit(0);
        }
        System.out.println(" --- FIM ---- ");

        long fim = System.currentTimeMillis();
        System.out.println("Execucao total -> Tempo em milis " + (fim - inicio));
    }

    public static String encontrarChave() {
        int tamanhoChave = 1;
        String chaveCompleta = "";
        List<String> letrasFrequentes = new ArrayList<>();
        List<Integer> deslocamentos = new ArrayList<>();
        int tamChaveProvavel = 1;
        double menorDiff = 10.0;

        int max = Math.min(textoCifrado.length, letras.length);
        for (int i = tamanhoChave; i <= max; i++) {
            List<List<String>> substrings = getSubstrings(i);

            System.out.println("- Tamanho da chave: " + i);

            //calcula IC para todas as substrings
            for (int j = 0; j < substrings.size(); j++) {
                double substringIc = getIc(substrings.get(j));
                System.out.println("  " + substringIc + " ==> " + j);
                System.out.println("  substringIC-icPT: " + (substringIc - icPt) + " ==> " + j);
                double localDiff = substringIc - icPt;
                if (localDiff <= 0.009 && localDiff > 0) { //TODO ver se isso ta certo
                    //if(localDiff > 0 && localDiff < menorDiff) {
                    tamanhoChave = i;
                    break;
                    //  tamChaveProvavel = i;
                    //menorDiff = localDiff;
                }
            }
            if (tamanhoChave != 1) break;
        }

        List<List<String>> substringsFinal = getSubstrings(tamanhoChave);

        for (int i = 0; i < substringsFinal.size(); i++) {
            letrasFrequentes.add(getLetraMaisFrequente(substringsFinal.get(i)));
            int diff = letrasMap.get(letrasFrequentes.get(i)) - letrasMap.get("a"); //TODO trocar o "a" por letra mais frequente pra usar
            if (diff < 0) {
                diff += 26;
            }

            deslocamentos.add(diff);
            chaveCompleta = chaveCompleta.concat(letras[diff]);
        }

        System.out.println("Letras mais frequentes das substrings: " + letrasFrequentes);
        System.out.println("Deslocamentos por substring:" + deslocamentos);
        System.out.println("Chave encontrada " + chaveCompleta);
        return chaveCompleta;
    }

    private static List<List<String>> getSubstrings(int tamanhoChave) {
        //TODO trocar pra List<String[]> e ver se melhora performance
        List<List<String>> substrings = new ArrayList<>();
        for (int i = 0; i < tamanhoChave; i++) {
            substrings.add(new ArrayList<>());
        }
        for (int index = 0; index < textoCifrado.length; index++) {
            int posicao = index % tamanhoChave;
            substrings.get(posicao).add(textoCifrado[index]);
        }
        return substrings;
    }

    private static double getIc(List<String> string) {
        long tamanho = 0;
        double fi = 0;

        for (int i = 0; i < letras.length; i++) {
            String letra = letras[i];
            long qtdOcorrencias = string.stream().filter(it -> it.equals(letra)).count();
            if (qtdOcorrencias > 0) {
                tamanho += qtdOcorrencias;
                fi += qtdOcorrencias * (qtdOcorrencias - 1);
            }
        }
        long n = tamanho * (tamanho - 1);
        double v = fi / n;
        return v;
//        String result = "";
//        for (String s: string) {
//            result = result.concat(s);
//        }
//        return calculate(result);
    }

    private static String getLetraMaisFrequente(List<String> string) {
        String maisComum = "";
        long maiorQtdOcorrencias = -1;
        long ocorrencias = 0;
        for (int i = 0; i < letras.length; i++) {
            String letra = letras[i];
            ocorrencias = string.stream().filter(it -> it.equals(letra)).count();
            if (ocorrencias > maiorQtdOcorrencias) {
                maisComum = letra;
                maiorQtdOcorrencias = ocorrencias;
            }
        }
        return maisComum;
    }

    private static void leituraArquivo() throws IOException {
        File f = new File("files/cipher1.txt");
        BufferedReader in = new BufferedReader(new FileReader(f));
        String st = in.readLine();
        textoCifrado = new String[st.length()];
        for (int i = 0; i < st.length(); i++) {
            char aux = st.charAt(i);
            textoCifrado[i] = (Character.toString(aux));
        }
    }

    private static String descriptografar(String chave) {
        long inicio = System.currentTimeMillis();
        String descriptografado = "";
        int indexChave = 0;
        for (String l : textoCifrado) {
            if (indexChave == chave.length()) indexChave = 0;

            int letraDescriptografada = letrasMap.get(l) - letrasMap.get(String.valueOf(chave.charAt(indexChave)));

            if (letraDescriptografada < 0) {
                letraDescriptografada += letras.length;
            }

            descriptografado = descriptografado.concat(letras[letraDescriptografada]);
            indexChave++;
        }

        long fim = System.currentTimeMillis();
        System.out.println("Tempo em milis " + (fim - inicio));
        return descriptografado;
    }

    private static void escreverTexto(String texto) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("descriptografia.txt"));
        writer.write(texto);
        writer.close();
    }
}



