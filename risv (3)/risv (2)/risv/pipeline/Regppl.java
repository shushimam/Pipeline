package pipeline;

import java.util.HashMap;
import java.util.Map;

import modulo.Inst;

public class Regppl {
	Inst inst;
	Map<String, Integer> sinaisControle;
	int aluResult;
	int memoryData;
	int pc;
	
	Regppl(){
		this.sinaisControle = new HashMap<>();
	}
	
	void imprimirConteudo(String nomeEstagio) {
		System.out.println("Estagio: " + nomeEstagio);
		
		if(inst != null) {
			System.out.println("Instrucao: " + inst.getType());
			System.out.println("Opcode: " + inst.getOpcode());
			System.out.println("Rs1: " + inst.getRs1());
			System.out.println("Rs2: " + inst.getRs2());
			System.out.println("Rd: " + inst.getRd());
			System.out.println("Func3: " + inst.getFunc3());
			System.out.println("Func7: " + inst.getFunc7());
			System.out.println("Imm: " + inst.getImm());
			
		}
		else {
			System.out.println("Sem instrucao");
		}
		
		System.out.println("Sinais de Controle: " + sinaisControle);
		System.out.println("Memory Data: " + memoryData);
		System.out.println("PC: " + pc);
		System.out.println();
	}
	void imprimirConteudoEX(String nomeEstagio) {
		System.out.println("Estagio: " + nomeEstagio);

		if (inst != null) {
			System.out.println("Instrucao: " + inst.getType());
			System.out.println("Opcode: " + inst.getOpcode());
			System.out.println("Rs1: " + inst.getRs1());
			System.out.println("Rs2: " + inst.getRs2());
			System.out.println("Rd: " + inst.getRd());
			System.out.println("Func3: " + inst.getFunc3());
			System.out.println("Func7: " + inst.getFunc7());
			System.out.println("Imm: " + inst.getImm());

		} else {
			System.out.println("Sem instrucao");
		}

		System.out.println("Sinais de Controle: " + sinaisControle);
		System.out.println("Resultado da ALU: " + aluResult);
		System.out.println("PC: " + pc);
		System.out.println();
	}
	void imprimirConteudoMEM(String nomeEstagio){
		System.out.println("Estagio: " + nomeEstagio);

		if (inst != null) {
			System.out.println("Instrucao: " + inst.getType());
			System.out.println("Opcode: " + inst.getOpcode());
			System.out.println("Rs1: " + inst.getRs1());
			System.out.println("Rs2: " + inst.getRs2());
			System.out.println("Rd: " + inst.getRd());

		} else {
			System.out.println("Sem instrucao");
		}

		System.out.println("Sinais de Controle: " + sinaisControle);
		System.out.println("Resultado da ALU: " + aluResult);
		System.out.println("PC: " + pc);
		System.out.println();
	}

	void imprimirConteudoWB(String nomeEstagio){
		System.out.println("Estagio: " + nomeEstagio);

		if (inst != null) {
			System.out.println("Instrucao: " + inst.getType());
			System.out.println("Opcode: " + inst.getOpcode());
			System.out.println("Rs2: " + inst.getRs2());
			System.out.println("Rd: " + inst.getRd());

		} else {
			System.out.println("Sem instrucao");
		}

		System.out.println("Sinais de Controle: " + sinaisControle);

		System.out.println();
	}


}
