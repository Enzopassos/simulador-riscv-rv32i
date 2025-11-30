import javax.swing.SwingUtilities;

public class RiscVSimulator {
    public static void main(String[] args) {
        Memory mem = new Memory();
        Bus bus = new Bus(mem);
        ALU alu = new ALU();
        CPU cpu = new CPU(bus, alu);

        int[] programa = {
            0x000800B7,
            0x05200113,
            0x0020A023,
            0x04900113,
            0x0020A223,
            0x05300113,
            0x0020A423,
            0x04300113,
            0x0020A623,
            0x02D00113,
            0x0020A823,
            0x05600113,
            0x0020AA23,
            0x00000013 
        };

        for (int i = 0; i < programa.length; i++) {
            mem.writeWord(i * 4, programa[i]);
        }

        System.out.println("Iniciando Interface Grafica...");
        
        SwingUtilities.invokeLater(() -> {
            new SimulatorGUI(cpu, bus, cpu.getRegisters());
        });
    }
}