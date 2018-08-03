import sys
with open(sys.argv[1],'r') as f1:
    with open(sys.argv[2],'r') as f2:
        with open(sys.argv[3],'w') as out:
            counter = 0
            print("Reading f1")
            f1_lines = f1.readlines()
            print("Read f1 complete. Total "+ str(len(f1_lines)-1)+" entries")
            print("Reading f2")
            f2_lines = f2.readlines()
            print("Read f2 complete. Total "+ str(len(f2_lines)-1)+" entries")
            for f1_line in f1_lines:
                if counter == 0:
                    entry_num, dimension = f1_line.split()
                    entry_sum = int(entry_num)+ int(f2_lines[0].split()[0])
                    out.write(str(entry_sum)+"\t"+dimension+"\n")
                    counter+=1
                else:
                    line = f1_line.split()
                    line[0]=""+line[0]
                    outline = " ".join(line)+"\n"
                    out.write(outline)
            counter = 0
            print("read 2") 
            for f2_line in f2_lines[1:]:
                line = f2_line.split()
                line[0] = "" + line[0]
                outline = " ".join(line) + "\n"
                out.write(outline)
