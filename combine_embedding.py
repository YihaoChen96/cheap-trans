with open('/home/yihaoc/software/MUSE/dumped/debug/7vjr2dpeh5/vectors-en.txt','r',encoding='utf-8') as en:
    with open('/home/yihaoc/software/MUSE/dumped/debug/7vjr2dpeh5/vectors-es.txt','r',encoding='utf-8') as es:
        with open("/home/yihaoc/data/vectors-en-es.txt",'w',encoding='utf-8') as out:
            counter = 0
            for en_line in en.eadlines():
                if counter == 0:
                    entry_num, dimension = en_line.split()
                    entry_sum = int(entry_num)+ int(es.readline().split()[0])
                    out.write(entry_sum+"\t",+dimension)
                    counter+=1
                else:
                    line = en_line.split()
                    line[0]="en_"+line[0]
                    outline = "\t".join(line)+"\n"
                    out.write(outline)
            counter = 0
            for es_line in es.eadlines():
                if counter == 0:
                    counter+=1
                    continue
                else:
                    line = es_line.split()
                    line[0] = "es_" + line[0]
                    outline = "\t".join(line) + "\n"
                    out.write(outline)