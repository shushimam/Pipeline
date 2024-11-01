package pipeline;

import java.io.IOException;
import java.util.*;

import modulo.Inst;

public class Pipeline {
	// Arrays para simular memória e registradores
	static int[] memoria = new int[32];
	static int[] registradores = new int[32];

	// Contador de programa
	static int pc = 0;

	// Registradores de pipeline para cada estágio
	static Regppl ifID = new Regppl();
	static Regppl idEx = new Regppl();
	static Regppl exMem = new Regppl();
	static Regppl memWb = new Regppl();
	static Regppl Regaux = new Regppl();

	// Registradores temporários para o próximo ciclo
	static Regppl nextIFID = new Regppl();
	static Regppl nextIDEX = new Regppl();
	static Regppl nextEXMEM = new Regppl();
	static Regppl nextMEMWB = new Regppl();

	public static void main(String[] args) {
		// Inicializa os registradores com valor 1 e a memória com valor 0
		for (int i = 0; i < 32; i++) {
			registradores[i] = 1;
			memoria[i] = 0;
		}

		// Lista de instruções a serem processadas pelo pipeline
		List<Inst> lista_instrucoes = Arrays.asList(
				new Inst("I", 19, 3, 0, 4, 0, 0, 100), // addi x4, x1, 100
				new Inst("R", 0, 3, 1, 3, 0, 0, 0),   // add x3, x3, x1
				new Inst("R", 2, 3, 1, 3, 0, 0, 0),   // sub x3, x3, x1
				new Inst("l", 3, 6, 0, 7, 0, 0, 20),  // lw x7, 20(x6)
				new Inst("S", 19, 4, 5, 0, 0, 0, 0)   // sw x5, 0(x4)
		);

		int ciclo = 0;
		// Loop principal do pipeline, executa enquanto houver instruções a processar
		while (pc / 4 < lista_instrucoes.size() || exec()) {
			System.out.println("CICLO: " + ciclo + "\n");

			// Executa cada estágio do pipeline
			WB();
			MEM();
			EX();
			ID();
			IF(lista_instrucoes);

			// Atualiza os registradores do pipeline ao final do ciclo
			atualizarRegistradoresPipeline();

			ciclo++;
			System.out.println("Pressione Enter para continuar...");
			try {
				while (System.in.read() != '\n');  // Espera até o usuário pressionar Enter
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Continuando a execução do programa...");
		}

		// Exibe o estado final dos registradores e da memória
		for (int i = 0; i < 32; i++) {
			System.out.println("registrador[" + i + "] = " + registradores[i]);
		}
		for (int i = 0; i < 32; i++) {
			System.out.println("memoria[" + i + "] = " + memoria[i]);
		}
	}

	// Verifica se algum registrador de pipeline está ocupado
	static boolean exec() {
		return ifID.inst != null || idEx.inst != null ||
				exMem.inst != null || memWb.inst != null;
	}

	// Estágio IF: Busca instrução da memória de instruções
	static void IF(List<Inst> lista_instrucoes) {
		if (pc / 4 < lista_instrucoes.size()) {
			nextIFID.inst = lista_instrucoes.get(pc / 4);
			nextIFID.pc = pc;
			pc += 4;
		}
		nextIFID.imprimirConteudo("IF");
	}

	// Estágio ID: Decodifica a instrução e lê os registradores necessários
	static void ID() {
		nextIDEX.inst = ifID.inst; nextIDEX.pc = ifID.pc;
		if (nextIDEX.inst != null) {
			// Define sinais de controle baseados no tipo da instrução
			if (nextIDEX.inst.getType().equals("R")) {
				nextIDEX.sinaisControle.put("RegWrite", 1);
				nextIDEX.sinaisControle.put("ALUOp", 2);
				nextIDEX.sinaisControle.put("ALUsrc", 0);
				nextIDEX.sinaisControle.put("MentoReg", 0);
			} else if (nextIDEX.inst.getType().equals("I")) {
				nextIDEX.sinaisControle.put("RegWrite", 1);
				nextIDEX.sinaisControle.put("ALUOp", 1);
				nextIDEX.sinaisControle.put("ALUsrc", 1);
				nextIDEX.sinaisControle.put("MentoReg", 0);
			} else if (nextIDEX.inst.getType().equals("S")) {
				nextIDEX.sinaisControle.put("MemWrite", 1);
				nextIDEX.sinaisControle.put("ALUOp", 0);
				nextIDEX.sinaisControle.put("ALUsrc", 1);
				nextIDEX.sinaisControle.put("MentoReg", 0);
			} else if (nextIDEX.inst.getType().equals("B")) {
				nextIDEX.sinaisControle.put("Branch", 1);
				nextIDEX.sinaisControle.put("ALUOp", 3);
				nextIDEX.sinaisControle.put("ALUsrc", 0);
				nextIDEX.sinaisControle.put("MentoReg", 0);
			} else if (nextIDEX.inst.getType().equals("l")) {
				nextIDEX.sinaisControle.put("MemRead", 1);
				nextIDEX.sinaisControle.put("ALUOp", 0);
				nextIDEX.sinaisControle.put("ALUsrc", 1);
				nextIDEX.sinaisControle.put("MentoReg", 1);
			}
		}
		ifID.inst = null; nextIDEX.imprimirConteudo("ID");
	}

	// Estágio EX: Executa operações da ALU
	static void EX() {
		nextEXMEM.inst = idEx.inst;
		nextEXMEM.pc = idEx.pc;
		nextEXMEM.sinaisControle = idEx.sinaisControle;

		if (nextEXMEM.inst != null) {
			// Executa operação da ALU baseada no sinal de controle
			if (nextEXMEM.sinaisControle.get("ALUOp") == 2) {
				if (nextEXMEM.inst.getOpcode() == 2) {
					nextEXMEM.aluResult = registradores[nextEXMEM.inst.getRs1()] + registradores[nextEXMEM.inst.getRs2()];
				} else if (nextEXMEM.inst.getOpcode() == 0) {
					nextEXMEM.aluResult = registradores[nextEXMEM.inst.getRs1()] - registradores[nextEXMEM.inst.getRs2()];
				}
			} else if (nextEXMEM.sinaisControle.get("ALUOp") == 1) {
				nextEXMEM.aluResult = registradores[nextEXMEM.inst.getRs1()] + nextEXMEM.inst.getImm();
			} else if (nextEXMEM.sinaisControle.get("ALUOp") == 3) {
				nextEXMEM.aluResult = (registradores[nextEXMEM.inst.getRs1()] == registradores[nextEXMEM.inst.getRs2()]) ? 1 : 0;
				if (nextEXMEM.aluResult == 1) {
					pc = nextEXMEM.pc + nextEXMEM.inst.getImm();
					// Limpa os registradores do pipeline para simular um flush
					ifID.inst = null;
					idEx.inst = null;
					nextEXMEM.inst = null;
				}
			} else if (nextEXMEM.sinaisControle.get("ALUOp") == 0) {
				nextEXMEM.aluResult = registradores[nextEXMEM.inst.getRs1()] + registradores[nextEXMEM.inst.getImm()];
			}
		}
		idEx.inst = null;
		nextEXMEM.imprimirConteudoEX("EX");
	}

	// Estágio MEM: Acessa a memória para operações de load/store
	static void MEM() {
		nextMEMWB.inst = exMem.inst;
		nextMEMWB.pc = exMem.pc;
		nextMEMWB.sinaisControle = exMem.sinaisControle;
		nextMEMWB.aluResult = exMem.aluResult;

		if (nextMEMWB.inst != null) {
			// Executa operação de escrita na memória se sinal de controle MemWrite estiver ativo
			if (nextMEMWB.sinaisControle.get("MemWrite") != null && nextMEMWB.sinaisControle.get("MemWrite") == 1) {
				int rs1 = nextMEMWB.inst.getRs1();
				int Imm = nextMEMWB.inst.getImm();
				int index = rs1 + Imm;
				if (index >= 0 && index < memoria.length) {
					System.out.println("valor rs1= " + index);
					memoria[nextMEMWB.inst.getRs2()] = registradores[index];
				} else {
					System.out.println("Erro: Índice " + index + " está fora dos limites do array memória.");
				}
			}

			// Executa operação de leitura da memória se sinal de controle MemRead estiver ativo
			if (nextMEMWB.sinaisControle.get("MemRead") != null && nextMEMWB.sinaisControle.get("MemRead") == 1) {
				int rs1 = nextMEMWB.inst.getRs1();
				int Imm = nextMEMWB.inst.getImm();
				int index = rs1 + Imm;
				System.out.println("valor do index" + index);
				if (index >= 0 && index < memoria.length) {
					nextMEMWB.aluResult = memoria[index];
				} else {
					System.out.println("Erro: Índice " + "index está fora da memória.");
				}
			}
		}
		for (int i = 0; i < 32; i++) {
			System.out.println("memoria[" + i + "] = " + memoria[i]);
		}
		exMem.inst = null;
		nextMEMWB.imprimirConteudoMEM("MEM");
	}

	// Estágio WB: Escreve resultado de volta no registrador
	static void WB() {
		Regaux.inst = memWb.inst;
		Regaux.sinaisControle = memWb.sinaisControle;
		Regaux.aluResult = memWb.aluResult;

		if (Regaux.inst != null && Regaux.sinaisControle.get("RegWrite") != null) {
			// Se MentoReg não estiver ativo, escreve o resultado da ALU no registrador
			if (Regaux.sinaisControle.get("MentoReg") != 1) {
				int rd = Regaux.inst.getRd();
				if (rd >= 0 && rd < registradores.length && !Regaux.inst.getType().equals("S")) {
					registradores[rd] = Regaux.aluResult;
				} else {
					System.out.println("Erro: Índice " + rd + " está fora dos limites do array registradores.");
				}
			} else if (Regaux.inst.getType().equals("l")) {
				int rd = Regaux.inst.getRd();
				registradores[rd] = memoria[Regaux.aluResult];
			}

		}
		for (int i = 0; i < 32; i++) {
			System.out.println("registrador[" + i + "] = " + registradores[i]);
		}
		Regaux.imprimirConteudoWB("WB");
		memWb.inst = null;
		Regaux.inst = null;
	}

	// Função para atualizar os registradores pipeline ao final de cada ciclo
	static void atualizarRegistradoresPipeline() {
		ifID = nextIFID;
		idEx = nextIDEX;
		exMem = nextEXMEM;
		memWb = nextMEMWB;

		// Limpa os registradores temporários para o próximo ciclo
		nextIFID = new Regppl();
		nextIDEX = new Regppl();
		nextEXMEM = new Regppl();
		nextMEMWB = new Regppl();
	}
}
