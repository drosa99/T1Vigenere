package br.drosa99;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Vigenere {

    private List<String> letras = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
    private double icPtT = 0.072723;
    private int ic = 0;
    private List<String> textoCifrado = Arrays.asList("a", "b", "c", "d");


    public static void main(String[] args) {
	// write your code here
    }

    public String encontrarChave(){
        int tamanhoChave = 1;
        String chaveCompleta = "";
        List<String> letrasFrequentes = new ArrayList<>();
        List<String> deslocamentos = new ArrayList<>();

        for(int i = tamanhoChave; i <= letras.size(); i ++){
            List<String> substrings = getSubstrings(i);
            System.out.println("- Tamanho da chave: " + i);


        }


    }

    private List<String> getSubstrings(int tamanhoChave) {
        List<String> substrings = new ArrayList<>();
        for (int index = 0; index < textoCifrado.size(); index ++){
            int posicao = index % tamanhoChave;
            substrings.set(posicao, textoCifrado.get(index));
        }
        return substrings;
    }
}
