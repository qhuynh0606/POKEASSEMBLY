# POKEASSEMBLY
Custom assembly language that simulates a Pokemon battle
A brief README (a few lines) listing:

    Which instructions are implemented.

    How to run/test them in MARS LE (e.g., “copy .class files here, use opcode X, etc.”).

Instructions Implemented:
* hpup $t1, imm                  - Heal HP by immediate amount
* hpdown $t1, imm                - Apply damage (HP = HP - imm)
* heal $t1, imm                  - hpup but as a move
* atkup $t1, imm                 - Increase attack by immediate amount
* defup $t2, imm                 - Increase defense by immediate amount
* cpy $t1,$t2                    - Copy value from $t2 -> $t1
* atk $t2, $t1, $t5              - Compute (raw) damage from 0 to atk - def
* appdmg $t3,$t2                 - Apply damage to HP (hp -= dmg)
* faint $t1, label               - Branch  label if HP <= 0
* bstatus $t1, label             - Branch if the status register is nonzero
* crit $t1                       - Critical hit, double the value in register
* statset $t1, imm               - Set status using mask
* statclr $t1, imm               - Clear status bits using immediate mask
* weather $s0, imm               - Set the weather environment
* typeadv $tDMG, $tMove, $tUser  - Apply type advantage multiplier to damage (Simple Fire/Water/Grass)
* stab $tDmg, $tMove, $tUser     - Same-Type Attack Bonus (1.5×)
* switch $tA, $tB                - Swap values (switch Pokémon).
* gheal $t1                      - Heal party registers (t0–t7) by amount in $t1.
* catch $t1, $t3                 - Computes chances of capture based on HP (255-hp)
* trainer label                  - prints a message stored at a label

To Run:
1. Place PokeAssembly.java into your customlangs folder
2. From MARS-LE directory, run:
   java -jar BuildCustomLang.jar PokeAssembly.java
3. Launch MARS LE, found in MARS-LE Folder
4. Switch language to Poke Assembly in the tools section
5. Write your programs

Included Programs: 
PokeAssembly.java
PokeAssembly.jar
Poke_ex1.asm
Poke_ex2.asm
Poke_ex3.asm
[INSERT GOOGLE SLIDES HERE]
