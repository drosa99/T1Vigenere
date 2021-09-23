package br.drosa99;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vigenere {

    private static List<String> letras = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
    private static double icPt = 0.072723;
    private static int ic = 0;
    //private static List<String> textoCifrado = Arrays.asList("a", "t", "a", "c", "a", "r", "a", "o");
    private static List<String> textoCifrado = Arrays.asList("l", "b", "l", "k", "l", "z", "l", "w");


    public static void main(String[] args) {
	    //TODO fazer leitura do arquivo
        System.out.println("Chave encontrada " + encontrarChave());
    }

    public static String encontrarChave(){
        int tamanhoChave = 1;
        String chaveCompleta = "";
        List<String> letrasFrequentes = new ArrayList<>();
        List<Integer> deslocamentos = new ArrayList<>();
        int tamChaveProvavel = 1;
        double menorDiff = 10.0;

        int max = Math.min(textoCifrado.size(), letras.size());
        for(int i = tamanhoChave; i <= max; i ++){
            List<List<String>> substrings = getSubstrings(i );

            System.out.println("- Tamanho da chave: " + i);

            //calcula IC para todas as substrings
            for (int j = 0; j < substrings.size(); j++) {
                double substringIc = getIc(substrings.get(j));
                System.out.println("  "  + substringIc + " ==> " + j);
                System.out.println("  substringIC-icPT: " +  (substringIc - icPt) + " ==> " + j);
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
            int diff = letras.indexOf(letrasFrequentes.get(i)) - letras.indexOf("a"); //TODO trocar o "a" por letra mais frequente pra usar
            if(diff < 0) {
                diff += 26;
            }

            deslocamentos.add(diff);
            chaveCompleta = chaveCompleta.concat(letras.get(diff));
        }

        System.out.println("Letras mais frequentes das substrings: " + letrasFrequentes);
        System.out.println("Deslocamentos por substring:" + deslocamentos);
        System.out.println("Chave encontrada" + chaveCompleta);
        return chaveCompleta;
    }

    private static List<List<String>> getSubstrings(int tamanhoChave) {
        List<List<String>> substrings = new ArrayList<>();
        for (int i = 0; i < tamanhoChave; i++) {
            substrings.add(new ArrayList<>());
        }
        for (int index = 0; index < textoCifrado.size(); index ++){
            int posicao = index % tamanhoChave;
            substrings.get(posicao).add(textoCifrado.get(index));
        }
        return substrings;
    }

    private static double getIc(List<String> string) {
        int tamanho = 0;
        double fi = 0;

        for (int i = 0; i < letras.size(); i++) {
            String letra = letras.get(i);
            long qtdOcorrencias = string.stream().filter(it -> it.equals(letra)).count();
            if(qtdOcorrencias > 0){
                tamanho += qtdOcorrencias;
                fi += qtdOcorrencias * (qtdOcorrencias - 1);
            }
        }
        double n = tamanho * (tamanho - 1);
        double v = fi / n;
        return v;
    }

    private static String getLetraMaisFrequente(List<String> string){
        String maisComum = "";
        long maiorQtdOcorrencias = -1;
        long ocorrencias = 0;
        for (int i = 0; i < letras.size(); i++) {
            String letra = letras.get(i);
            ocorrencias = string.stream().filter(it -> it.equals(letra)).count();
            if (ocorrencias > maiorQtdOcorrencias) {
                maisComum = letra;
                maiorQtdOcorrencias = ocorrencias;
            }
        }
        return maisComum;
    }

    //TODO fazer leitura de arquivo
    //TODO suporta para ingles


}
