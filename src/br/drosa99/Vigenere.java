package br.drosa99;

import java.io.*;
import java.util.*;

public class Vigenere {

    private static String[] letras = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private static HashMap<String, Integer> letrasMap = new HashMap<>();

    private static final double icPt = 0.072723;
    private static boolean isPt = true;
    private static String[] textoCifrado;
    private static final double icEn = 0.067;

    //TODO fazer suporte para ingles

    public static void main(String[] args) {

        String filename = args.length == 1 ? args[0] : "cipher1.txt";


        for (int i = 0; i < letras.length; i++) {
            letrasMap.put(letras[i], i);
        }

        long inicio = System.currentTimeMillis();
        try {
            leituraArquivo(filename);
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo do texto criptografado.");
            System.exit(0);
        }


        String chave = encontrarChave();
        System.out.println(" Iniciada a decriptografia do texto utilizando a chave encontrada ..... ");
        String textoDescriptografado = decriptografar(chave);
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

        int max = Math.min(textoCifrado.length, 20);
        for (int i = tamanhoChave; i <= max; i++) {
            List<List<String>> substrings = buscarSubstrings(i);

            System.out.println("- Tamanho da chave: " + i);

            //aqui calcula IC para todas as substrings
            for (int j = 0; j < substrings.size(); j++) {
                double substringIc = buscarIC(substrings.get(j));
//                System.out.println("  " + substringIc + " ==> " + j);
//                System.out.println("  substringIC-icPT: " + (substringIc - icPt) + " ==> " + j);
                double diffPt = substringIc - icPt;
                if (diffPt <= 0.009 && diffPt > 0) {
                    tamanhoChave = i;
                    break;
                }
                double diffEn = substringIc - icEn;
                if (diffEn <= 0.009 && diffEn > 0) {
                    tamanhoChave = i;
                    isPt = false;
                    break;
                }
            }
            if (tamanhoChave != 1) break;
        }

        List<List<String>> substringsFinal = buscarSubstrings(tamanhoChave);

        for (int i = 0; i < substringsFinal.size(); i++) {
            String letraMaisFrequente = buscarLetraMaisFrequente(substringsFinal.get(i));
            letrasFrequentes.add(letraMaisFrequente);
            String letraMaisFrequenteLingua = isPt ? "a" : "e";
            int diff = letrasMap.get(letraMaisFrequente) - letrasMap.get(letraMaisFrequenteLingua);
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

    private static List<List<String>> buscarSubstrings(int tamanhoChave) {
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

    private static double buscarIC(List<String> string) {
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
    }

    private static String buscarLetraMaisFrequente(List<String> string) {
        String maisComum = "";
        long maiorQtdOcorrencias = -1;
        long ocorrencias = 0;
        for (String letra : letras) {
            ocorrencias = string.stream().filter(it -> it.equals(letra)).count();
            if (ocorrencias > maiorQtdOcorrencias) {
                maisComum = letra;
                maiorQtdOcorrencias = ocorrencias;
            }
        }
        return maisComum;
    }

    private static void leituraArquivo(String filename) throws IOException {
        File f = new File("files/"  + filename);
        BufferedReader in = new BufferedReader(new FileReader(f));
        String st = in.readLine();
        textoCifrado = new String[st.length()];
        for (int i = 0; i < st.length(); i++) {
            char aux = st.charAt(i);
            textoCifrado[i] = (Character.toString(aux));
        }
    }

    private static String decriptografar(String chave) {
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
        BufferedWriter writer = new BufferedWriter(new FileWriter("resultado.txt"));
        writer.write(texto);
        writer.close();
    }
}



