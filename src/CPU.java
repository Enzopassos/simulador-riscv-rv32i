import java.io.IOException;

public class CPU {
    private int pc;
    private final Registers registers;
    private final Bus bus;
    private final ALU alu;
    
    private static final int VRAM_REFRESH_RATE = 5;

    public CPU(Bus bus, ALU alu) {
        this.pc = 0;
        this.registers = new Registers();
        this.bus = bus;
        this.alu = alu;
    }

    public void run(int instructionCount) {
        System.out.println("--- Inicio da Execucao (Limitada a " + instructionCount + " ciclos) ---");
        
        for (int i = 0; i < instructionCount; i++) {
            
            checkInterrupt();

            step();
            
            if ((i + 1) % VRAM_REFRESH_RATE == 0) {
                bus.dumpVRAM();
            }
        }
        System.out.println("\n--- Fim da Execucao ---");
    }
    
    private void checkInterrupt() {
        try {
            if (System.in.available() > 0) {
                int key = System.in.read(); 
                System.out.println("\n[INTERRUPCAO DE HARDWARE] Tecla detectada: '" + (char)key + "'");
            }
        } catch (IOException e) {
        }
    }
    
    public void dumpRegisters() {
        registers.dump();
    }

    public void step() {
        int instruction = bus.load(pc);
        int nextPC = pc + 4; 

        int opcode = instruction & 0x7F;
        int rd     = (instruction >> 7) & 0x1F;
        int funct3 = (instruction >> 12) & 0x07;
        int rs1    = (instruction >> 15) & 0x1F;
        int rs2    = (instruction >> 20) & 0x1F;
        int funct7 = (instruction >> 25) & 0x7F;
        
        int immI = instruction >> 20; 
        
        int immS = ((instruction >> 25) << 5) | ((instruction >> 7) & 0x1F);
        if ((immS & 0x800) != 0) immS |= 0xFFFFF000; // Sign ext
        
        int immB = ((instruction >> 31) << 12) | ((instruction & 0x80) << 4) | 
                   ((instruction >> 8) & 0x0F) << 1 | ((instruction >> 25) & 0x3F) << 5; 
        if ((immB & 0x1000) != 0) immB |= 0xFFFFE000;

        int immU = instruction & 0xFFFFF000; 

        int immJ = ((instruction >> 31) << 20) | 
                   ((instruction >> 12) & 0xFF) << 12 | 
                   ((instruction >> 20) & 0x1) << 11 | 
                   ((instruction >> 21) & 0x3FF) << 1; 
        if ((immJ & 0x100000) != 0) immJ |= 0xFFE00000;

        int val1 = registers.read(rs1);
        int val2 = registers.read(rs2);
        
        try {
            switch (opcode) {
                case 0x33:
                    int res = 0;
                    if (funct7 == 0x00 || funct7 == 0x20) {
                        switch (funct3) {
                            case 0x0: res = (funct7 == 0x20) ? alu.sub(val1, val2) : alu.add(val1, val2); break;
                            case 0x1: res = alu.sll(val1, val2); break;
                            case 0x2: res = alu.slt(val1, val2); break;
                            case 0x3: res = alu.sltu(val1, val2); break;
                            case 0x4: res = alu.xor(val1, val2); break;
                            case 0x5: res = (funct7 == 0x20) ? alu.sra(val1, val2) : alu.srl(val1, val2); break;
                            case 0x6: res = alu.or(val1, val2); break;
                            case 0x7: res = alu.and(val1, val2); break;
                        }
                        registers.write(rd, res);
                    }
                    break;

                case 0x13:
                    switch (funct3) {
                        case 0x0: registers.write(rd, alu.add(val1, immI)); break;
                        case 0x1: registers.write(rd, alu.sll(val1, immI)); break;
                        case 0x2: registers.write(rd, (val1 < immI) ? 1 : 0); break;
                        case 0x3: registers.write(rd, (Integer.compareUnsigned(val1, immI) < 0) ? 1 : 0); break;
                        case 0x4: registers.write(rd, alu.xor(val1, immI)); break;
                        case 0x5: 
                            if ((immI & 0x400) != 0) registers.write(rd, alu.sra(val1, immI));
                            else registers.write(rd, alu.srl(val1, immI));
                            break;
                        case 0x6: registers.write(rd, alu.or(val1, immI)); break;
                        case 0x7: registers.write(rd, alu.and(val1, immI)); break;
                    }
                    break;
                
                case 0x03:
                    int addrLoad = alu.add(val1, immI);
                    int loadData = 0;
                    switch(funct3) {
                        case 0x0:
                            loadData = bus.loadByte(addrLoad); 
                            loadData = (byte) loadData;
                            break;
                        case 0x1:
                            loadData = bus.loadHalf(addrLoad);
                            loadData = (short) loadData;
                            break;
                        case 0x2:
                            loadData = bus.load(addrLoad);
                            break;
                        case 0x4:
                            loadData = bus.loadByte(addrLoad) & 0xFF;
                            break;
                        case 0x5:
                            loadData = bus.loadHalf(addrLoad) & 0xFFFF;
                            break;
                    }
                    registers.write(rd, loadData);
                    break;

                case 0x23:
                    int addrStore = alu.add(val1, immS);
                    switch(funct3) {
                        case 0x0:
                            bus.storeByte(addrStore, val2);
                            break;
                        case 0x1:
                            bus.storeHalf(addrStore, val2);
                            break;
                        case 0x2:
                            bus.store(addrStore, val2);
                            break;
                    }
                    break;

                case 0x63:
                    boolean takeBranch = false;
                    switch(funct3) {
                        case 0x0: takeBranch = (val1 == val2); break;
                        case 0x1: takeBranch = (val1 != val2); break;
                        case 0x4: takeBranch = (val1 < val2); break;
                        case 0x5: takeBranch = (val1 >= val2); break;
                        case 0x6: takeBranch = (Integer.compareUnsigned(val1, val2) < 0); break;
                        case 0x7: takeBranch = (Integer.compareUnsigned(val1, val2) >= 0); break;
                    }
                    if (takeBranch) {
                        nextPC = pc + immB;
                    }
                    break;
                    
                case 0x37:
                    registers.write(rd, immU);
                    break;
                    
                case 0x17:
                    registers.write(rd, pc + immU);
                    break;

                case 0x6F:
                    registers.write(rd, pc + 4);
                    nextPC = pc + immJ;
                    break;
                    
                case 0x67:
                    int target = (val1 + immI) & ~1; 
                    registers.write(rd, pc + 4);
                    nextPC = target;
                    break;
                    
                case 0x73:
                    if (immI == 0) System.out.println("[CPU] ECALL executado em PC=" + pc);
                    else if (immI == 1) System.out.println("[CPU] EBREAK executado em PC=" + pc);
                    break;
                    
                case 0x00:
                    break;
                    
                default:
                    System.out.println("Instrucao desconhecida (Opcode: 0x" + Integer.toHexString(opcode) + ")");
            }
        } catch (Exception e) {
            System.out.println("Erro fatal em PC=" + pc + ": " + e.getMessage());
            e.printStackTrace();
        }

        pc = nextPC;
    }
    
    public int getPC() { return pc; }

    public Registers getRegisters() {
        return this.registers;
    }
}