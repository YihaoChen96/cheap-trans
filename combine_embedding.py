import sys
with open(sys.argv[1],'r') as en:
    with open(sys.argv[2],'r') as es:
        with open(sys.argv[3],'w') as out:
            counter = 0
            print("read 1")
            for en_line in en.readlines():
                if counter == 0:
                    entry_num, dimension = en_line.split()
                    entry_sum = int(entry_num)+ int(es.readline().split()[0])
                    out.write(str(entry_sum)+"\t"+dimension+"\n")
                    counter+=1
                else:
                    line = en_line.split()
                    line[0]=""+line[0]
                    outline = " ".join(line)+"\n"
                    out.write(outline)
            counter = 0
            print("read 2") 
            for es_line in es.readlines():
                if counter == 0:
                    counter+=1
                    continue
                else:
                    line = es_line.split()
                    line[0] = "" + line[0]
                    outline = " ".join(line) + "\n"
                    out.write(outline)
