package modulo;

public class Inst {
	String type;
	int opcode;
	int rs1, rs2, rd;
	int func3, func7;
	int imm;
	
	public Inst(String type, int opcode, int rs1, int rs2, int rd, int func3, int func7, int imm){
		this.type = type;
		this.opcode = opcode;
		this.rs1 = rs1;
		this.rs2 = rs2;
		this.rd = rd;
		this.func3 = func3;
		this.func7 = func7;
		this.imm = imm;
	}

	public String getType() {
		return type;
	}

	public int getOpcode() {
		return opcode;
	}	

	public int getRs1() {
		return rs1;
	}

	public int getRs2() {
		return rs2;
	}

	public int getRd() {
		return rd;
	}

	public int getFunc3() {
		return func3;
	}

	public int getFunc7() {
		return func7;
	}

	public int getImm() {
		return imm;
	}

}