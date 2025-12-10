# PokeAssembly Example 2: Fire move with STAB, type advantage, and crit

.data
fire_msg:
.asciiz "You used a Fire-type move!\n"

super_msg:
.asciiz "It's super effective!\n"

faint_msg:
.asciiz "Opp fainted!\n"

alive_msg:
.asciiz "Opp survived! Hit him again\n"

.text
.globl main

# Conventions:
# $t0 = our HP
# $t1 = our ATK
# $t2 = our type       (1=Fire,2=Water,3=Grass)
# $t3 = enemy HP
# $t4 = enemy DEF
# $t5 = enemy type
# $t6 = damage

main:
# Initialize stats
hpup $t0, 60        # our HP = 60
atkup $t1, 25        # our ATK = 25
defup $t2, 0         # clear first, then set type in $t2
hpup $t2, 1         # our type = 1 (Fire)
hpup $t3, 50        # enemy HP = 50
defup $t4, 15        # enemy DEF = 15
defup $t5, 0         # clear first, then set
hpup $t5, 3         # enemy type = 3 (Grass)

# our move
trainer fire_msg

# Base damage calculation: dmg = max(0, ATK - DEF)
atk $t6, $t1, $t4        # $t6 = base damage

# Apply STAB (Same-Type Attack Bonus)
stab $t6, $t2, $t2       # if move type == user type, dmg * 1.5

# Apply Type Advantage
typeadv $t6, $t2, $t5	# Fire vs Grass -> dmg * 2

# Critical hit to seal the deal
crit $t6			# dmg *= 2

# Apply damage to enemy HP ---
appdmg $t3, $t6           # enemy HP -= dmg

# Print super effective message
trainer super_msg

# Check faint
faint $t3, enemy_fainted

# Enemy survived
trainer alive_msg

# Fake j end because I don't want to implement j end
hpup $t7, 1
bstatus $t7, end           # always branch

enemy_fainted:
trainer faint_msg

end:
