package mars.mips.instructions.customlangs;
import mars.mips.hardware.*;
import mars.*;
import mars.util.*;
import mars.mips.instructions.*;

public class PokeAssembly extends CustomAssembly{
    @Override
    public String getName(){
        return "Poke Assembly";
    }

@Override
public String getDescription(){
    return "Custom assembly language that simulates a Pokemon battle";
}

@Override
protected void populate(){
    // ================
    //  BASIC STAT/HP
    // ================

    // hpup: Heal HP in register by imm amount
    // Use: $t0, 50
    instructionList.add(
            new BasicInstruction("hpup $t1,-100",
             "Heal HP: Increase HP in ($t1) by an immediate value",
            BasicInstructionFormat.I_FORMAT,
            "001000 fffff 00000 ssssssssssssssss",
            new SimulationCode()
           {
               public void simulate(ProgramStatement statement) throws ProcessingException
              {
                 int[] operands = statement.getOperands();
                 int oldHp = RegisterFile.getValue(operands[0]);
                 int imm = operands[1] << 16 >> 16;
                 int newHp = oldHp + imm;
                 RegisterFile.updateRegister(operands[0], newHp);
              }
            
            }
        )
    );

    // hpdown: Apply dmg to register
    // Use: hpdown $t3, -20 (20 damage)
    instructionList.add(
            new BasicInstruction("hpdown $t1,-100",
             "Take Damage: Decrease HP in ($t1) by an immediate value",
            BasicInstructionFormat.I_FORMAT,
            "001001 fffff 00000 ssssssssssssssss",
            new SimulationCode()
           {
               public void simulate(ProgramStatement statement) throws ProcessingException
              {
                 int[] operands = statement.getOperands();
                 int hp = RegisterFile.getValue(operands[0]);
                 int imm = operands[1] << 16 >> 16;
                 int newHp = hp - imm; // negative imm as dmg magnitude
                 RegisterFile.updateRegister(operands[0], newHp);
              }
           }
        )
    );

    // heal: Restore HP using a move (similar to hpup, just from moves)
    // Use: heal $t0, 30
    instructionList.add(
            new BasicInstruction("heal $t1,-100",
             "heal : Restore HP in )$t1) by immediate value, as a move",
            BasicInstructionFormat.I_BRANCH_FORMAT,
            "001010 fffff 00000 ssssssssssssssss",
            new SimulationCode()
           {
               public void simulate(ProgramStatement statement) throws ProcessingException
              {        
                int[] operands = statement.getOperands();
                int hp = RegisterFile.getValue(operands[0]);
                int imm = operands[1] << 16 >> 16;
                int newHp = hp + imm;
                RegisterFile.updateRegister(operands[0], newHp);
              }          
           }
        )
    );

    // atkup: Increase attack stat by immediate amount
    // Use: atkup $t1, 2
    instructionList.add(
            new BasicInstruction("atkup $t1,-4",
            "atkup: Increase ATK stat in ($t1) by immediate value",
             BasicInstructionFormat.I_FORMAT,
            "001011 fffff 00000 ssssssssssssssss",
            new SimulationCode()
           {
               public void simulate(ProgramStatement statement) throws ProcessingException
              {
                 int[] operands = statement.getOperands();
                 int atk = RegisterFile.getValue(operands[0]);
                 int imm = operands[1] << 16 >> 16;
                 int newAtk = atk + imm;
                 RegisterFile.updateRegister(operands[0], newAtk);
              }
           }
        )
    );
    
    // defup: Increase defense stat by immediate amount
    // Use: defup $t2, 2
    instructionList.add(
        new BasicInstruction("defup $t1,-4",
        "atkup: Increase DEF stat in ($t1) by immediate value",
         BasicInstructionFormat.I_FORMAT,
        "001100 fffff 00000 ssssssssssssssss",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int def = RegisterFile.getValue(operands[0]);
             int imm = operands[1] << 16 >> 16;
             int newDef = def + imm;
             RegisterFile.updateRegister(operands[0], newDef);
          }
       }
    )
);

    // cpy: Copy a stat/value from one register to another
    // Use: cpy $t1,$t2 -> $t1 = $t2
    instructionList.add(
            new BasicInstruction("cpy $t1,$t2",
            "cpy: Copy value from ($t2) to ($t1)",
             BasicInstructionFormat.R_FORMAT,
            "000000 fffff sssss 00000 00000 000011",
            new SimulationCode()
           {
               public void simulate(ProgramStatement statement) throws ProcessingException
              {
                 int[] operands = statement.getOperands();
                 int src = RegisterFile.getValue(operands[1]);
                 RegisterFile.updateRegister(operands[0], src);
              }
            }
        )
    );

    // ====================
    //  DAMAGE CALCULATION
    // ====================

    // atk: Compute raw damage = max(0, atk - def)
    // Use: atk $t2, $t1, $t5   # dmg = ATK - OPP DEF
    instructionList.add(
        new BasicInstruction("atk $t1,$t2,$t3",
        "atk: Compute damage = max(0, ($t2 - $t3)) and store in ($t1)",
         BasicInstructionFormat.R_FORMAT,
        "000000 fffff sssss ttttt 00000 000001",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int rd = operands[0];
             int rs = operands[1];
             int rt = operands[2];
             int atk = RegisterFile.getValue(rs);
             int def = RegisterFile.getValue(rt);
             int dmg = atk - def;
             if (dmg<0) dmg = 0;
             RegisterFile.updateRegister(rd, dmg);
          }
        }
    )
);

    // appdmg: Subtract damage from HP
    // Use: appdmg $t3,$t2     # OPP HP -= dmg
    instructionList.add(
        new BasicInstruction("appdmg $t1,$t2",
        "appdmg: Apply damage in ($t2) to HP in ($t1)",
         BasicInstructionFormat.R_FORMAT,
        "000000 fffff sssss 00000 00000 000010",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int hpReg = operands[0];
             int dmgReg = operands[1];
             int hp = RegisterFile.getValue(hpReg);
             int dmg = RegisterFile.getValue(dmgReg);
             RegisterFile.updateRegister(hpReg, hp - dmg);
          }
        }
    )
);

    // crit: Double damage in register
    // Use: crit $t2
    instructionList.add(
        new BasicInstruction("crit $t1",
        "crit: Critical hit, double the value in ($t1)",
         BasicInstructionFormat.R_FORMAT,
        "000000 fffff 00000 00000 00000 010010",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int val = RegisterFile.getValue(operands[0]);
             RegisterFile.updateRegister(operands[0], val * 2);
          }
        }
    )
);
    
    // ===========
    //  BRANCHING
    // ===========

    // faint: Branch if HP <= 0
    // Use: faint $t3, opp_faint
    instructionList.add(
        new BasicInstruction("faint $t1, label",
        "faint: Branch to label if HP in ($t1) is 0 or less",
         BasicInstructionFormat.I_BRANCH_FORMAT,
        "000101 fffff 00000 ssssssssssssssss",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int hp = RegisterFile.getValue(operands[0]);
             if (hp <= 0) {
                Globals.instructionSet.processBranch(operands[1]);
             }
            }
        }
    )
);
    // bstatus: Branch if stats register is not zero
    // Use: bstatus $t6, label
    instructionList.add(
        new BasicInstruction("bstatus $t1, label",
        "bstatus: Branch to label if status in ($t1) is not zero",
         BasicInstructionFormat.I_BRANCH_FORMAT,
        "000101 fffff 00000 ssssssssssssssss",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int status = RegisterFile.getValue(operands[0]);
             if (status != 0) {
                Globals.instructionSet.processBranch(operands[1]);
             }
            }
        }
    )
);

    // ======================================================
    //  ASSORTED BATTLE CONDITIONS ( STATUS, TYPE, WEATHER )
    // ======================================================

    // statset: Set status using immediates
    // Use: statset $t6, 0x01    # add SLEEP
    instructionList.add(
        new BasicInstruction("statset $t1, -1",
        "statset: Set status in ($t1) using immediate mask",
         BasicInstructionFormat.I_FORMAT,
        "001101 fffff 00000 ssssssssssssssss",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int status = RegisterFile.getValue(operands[0]);
             int mask = operands[1] << 16 >> 16;
             RegisterFile.updateRegister(operands[0], status | mask);
            }
        }
    )
);

    // statclr: Clear status bits using immediate mask
    // Use: statclr $t6, 0x01    # remove SLEEP
    instructionList.add(
        new BasicInstruction("statclr $t1, -1",
        "statclr: Clear status in ($t1) using immediate mask",
         BasicInstructionFormat.I_FORMAT,
        "001110 fffff 00000 ssssssssssssssss",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int status = RegisterFile.getValue(operands[0]);
             int mask = operands[1] << 16 >> 16;
             RegisterFile.updateRegister(operands[0], status & ~mask);
            }
        }
    )
);

    // weather: Set the weather environment
    // Use: weather $s0, 2    # sunny weather
    instructionList.add(
        new BasicInstruction("weather $t1, -1",
        "statclr: Set weather/environment in ($t1)",
         BasicInstructionFormat.I_FORMAT,
        "001111 fffff 00000 ssssssssssssssss",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int imm = operands[1] << 16 >> 16;
             RegisterFile.updateRegister(operands[0], imm);
            }
        }
    )
);
    // ================================
    //  DAMAGE MODIFIERS (simple)
    //  types 1=Fire, 2=Water, 3=Grass
    // ================================

    // typeadv: Apply type advantage multiplier to damage
    // Use: typeadv $t2, $s4, $s3
    instructionList.add(
        new BasicInstruction("typeadv $t1, $t2, $t3",
        "typeadv: Modify dmg in ($t1) based on type ($t2) vs defendser type ($t3)",
         BasicInstructionFormat.R_FORMAT,
        "000000 fffff sssss ttttt 00000 010000",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int dmgReg = operands[0];
             int moveTypeReg = operands[1];
             int defTypeReg = operands[2];
             int dmg = RegisterFile.getValue(dmgReg);
             int moveType = RegisterFile.getValue(moveTypeReg);
             int defType = RegisterFile.getValue(defTypeReg);
             boolean strong = false;
             boolean weak = false;
             
             // Fire(1), Water(2), Grass(3)

             // TYPE ADVANTAGES
             // 1>3
             if (moveType == 1 && defType == 3) strong = true;
             // 3>2
             if (moveType == 3 && defType == 2) strong = true;
             // 2>1
             if (moveType == 2 && defType == 1) strong = true;

             // TYPE DISADVANTAGES
             // 1<2
             if (moveType == 1 && defType == 2) weak = true;
             // 2<3
             if (moveType == 2 && defType == 3) weak = true;
             // 3<1
             if (moveType == 3 && defType == 1) weak = true;

             // THE ACTUAL MULTIPLIERS
             if (strong) {
                dmg = dmg * 2;
             } else if (weak) {
                dmg = dmg / 2;
             }
             RegisterFile.updateRegister(dmgReg, dmg);

            }
        }
    )
);

    // stab: Same-Type Attack Bonus (1.5x damage)
    // Use: stab $t2, $s4, $s2
    instructionList.add(
        new BasicInstruction("stab $t1, $t2, $t3",
        "stab: if type ($t2) is same as defender type ($t3), boost damage in ($t1) by 1.5x",
         BasicInstructionFormat.R_FORMAT,
        "000000 fffff sssss ttttt 00000 010001",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int dmgReg = operands[0];
             int moveTypeReg = operands[1];
             int defTypeReg = operands[2];
             int dmg = RegisterFile.getValue(dmgReg);
             int moveType = RegisterFile.getValue(moveTypeReg);
             int defType = RegisterFile.getValue(defTypeReg);
             
             if (moveType == defType) {
                dmg = dmg + dmg / 2;
             RegisterFile.updateRegister(dmgReg, dmg);

            }
          }}
    )
);
    // ====================
    //  TEAM-BASED ACTIONS
    // ====================

    // switch: swap stats between 2 registers (switching pokemon)
    // Use: switch $t0, $t3    # swp stats
    instructionList.add(
        new BasicInstruction("switch $t1, $t2",
        "switch: Swap values between ($t1) and ($t2)",
         BasicInstructionFormat.R_FORMAT,
        "000000 fffff sssss 00000 00000 010011",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int rA = operands[0];
             int rB = operands[1];
             int valA = RegisterFile.getValue(rA);
             int valB = RegisterFile.getValue(rB);
             
             RegisterFile.updateRegister(rA, valA);
             RegisterFile.updateRegister(rB, valB);
            }
        }
    )
);

    // gheal: group heal, which restores X HP to entire team ($t0-$t7)
    // Use: gheal $t1     # heals by $t1
    instructionList.add(
        new BasicInstruction("gheal $t1",
        "gheal: Restore HP to entire team using amoun in ($t1)",
         BasicInstructionFormat.R_FORMAT,
        "000000 fffff 00000 00000 00000 010100",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int healReg = operands[0];
             int healAmt = RegisterFile.getValue(healReg);
             
             for (int i=0; i<16; i++) {
                int hp = RegisterFile.getValue(i);
                RegisterFile.updateRegister(i, hp + healAmt);
             }
            }
        }
    )
);

    // ==================
    //  MISC OTHER STUFF
    // ==================

    // catch: Computes chances of capture based on HP
    // Use: catch $t1, $t3     # $t3=OPP HP, $t1-chance
    instructionList.add(
        new BasicInstruction("catch $t1, $t2",
        "catch: Compute catch chance from HP in ($t2) in ($t1)",
         BasicInstructionFormat.R_FORMAT,
        "000000 fffff sssss 00000 00000 010101",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
             int[] operands = statement.getOperands();
             int chanceReg = operands[0];
             int hpReg = operands[1];

             int hp = RegisterFile.getValue(hpReg);
             int chance = 255 - hp;
             if (chance<0) chance=0;
             if(chance>255) chance=255;
             
             RegisterFile.updateRegister(chanceReg, chance);
            }
        }
    )
);

    // trainer: prints a message stored at a label (ex "Trainer set out ambiguous rat pokemon!")
    // Use: trainer vistory
    instructionList.add(
        new BasicInstruction("trainer label",
        "trainer: Prints a message store at given label",
         BasicInstructionFormat.I_BRANCH_FORMAT,
        "010000 00000 00000 ffffffffffffffff",
        new SimulationCode()
       {
           public void simulate(ProgramStatement statement) throws ProcessingException
          {
            char ch;

            // Get label token (2nd token: "trainer" then "label")
            String label = statement.getOriginalTokenList().get(1).getValue();

            // Look up the label in the symbol table to get its address
            int byteAddress = Globals.program.getLocalSymbolTable()
                                             .getAddressLocalOrGlobal(label);

            try {
                ch = (char) Globals.memory.getByte(byteAddress);

                // Print until NULL byte
                while (ch != 0) {
                    SystemIO.printString(Character.toString(ch));
                    byteAddress++;
                    ch = (char) Globals.memory.getByte(byteAddress);
                }
            } catch (AddressErrorException e) {
                throw new ProcessingException(statement, e);
            }
            }
        }
    )
);
}
}
