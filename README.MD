# Simulador de Computador RISC-V (RV32I)

> **Documentação Completa:** Para visualizar a versão formal com capa e formatação ABNT, acesse o arquivo [Documentacao_Simulador_RISCV.pdf](./Documentacao_Simulador_RISCV.pdf) neste repositório.

## 1. Visão Geral
Este projeto consiste na implementação de um simulador funcional da arquitetura RISC-V, especificamente o conjunto de instruções base de inteiros de 32 bits (RV32I). O simulador foi desenvolvido em Java, utilizando conceitos de Orientação a Objetos para modularizar os componentes de hardware e a biblioteca Java Swing para a interface gráfica.

## 2. Arquitetura do Sistema
O simulador foi estruturado em módulos independentes:

*   **CPU:** Responsável pelo controle do fluxo (Fetch-Decode-Execute).
*   **ALU:** Realiza operações matemáticas e lógicas.
*   **Registers:** Simula os 32 registradores (x0-x31).
*   **Memory:** Armazenamento linear endereçável a byte (Little Endian).
*   **Bus:** Barramento que conecta CPU, Memória e E/S.
*   **SimulatorGUI:** Interface Gráfica para visualização em tempo real.

## 3. Detalhes de Implementação

### 3.1. Conjunto de Instruções
A CPU suporta o conjunto RV32I completo, incluindo:
*   Aritmética/Lógica (ADD, SUB, AND, OR, XOR, Shifts).
*   Acesso à Memória (LB, LH, LW, SB, SH, SW).
*   Desvios (BEQ, BNE, JAL, JALR).

### 3.2. Mapeamento de Memória
O Barramento roteia os endereços conforme a tabela:

| Faixa de Endereço | Dispositivo | Descrição |
| :--- | :--- | :--- |
| **0x00000 - 0x7FFFF** | RAM Principal | Instruções e Dados. |
| **0x80000 - 0x8FFFF** | VRAM | Memória de Vídeo (Exibida na GUI). |
| **0x9FC00 - 0x9FFFF** | E/S Mapeada | Saída para o Terminal (System.out). |

## 4. Funcionalidades Extras
1.  **Interface Gráfica (GUI):** Visualização de registradores e VRAM.
2.  **Interrupção de Hardware:** Simulação de interrupção externa via teclado (`System.in`).
3.  **E/S Programada:** Atualização da tela a cada 5 instruções.

## 5. Testes Realizados e Validação
O sistema carrega automaticamente um programa de teste que:
1.  Calcula caracteres ASCII via ALU.
2.  Escreve "RISC-V" na VRAM.
3.  O resultado é verificado visualmente na interface.

## 6. Como Executar
Certifique-se de ter o **Java (JDK 8+)** instalado.

1.  Compile os arquivos:
    ```bash
    javac *.java
    ```
2.  Execute o simulador:
    ```bash
    java RiscVSimulator
    ```

---
**Autores:** Arthur Iwankiu Castro & Enzo da Silva Passos
**Instituição:** Universidade Católica de Santos - 2025