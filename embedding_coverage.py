from gensim.models import KeyedVectors
#word_vectors = KeyedVectors.load_word2vec_format('/shared/preprocessed/yihaoc/word2vec/wiki.ru.vec')
print("vector read complete")
outlines = []
with open("/shared/preprocessed/yihaoc/word2vec/wiki.ru.entry",'r') as vec:
    lines = vec.readlines()
    with open('/shared/corpora/ner/lorelei-swm-new/rus/column/rus.train.uniq','r') as inf:
        find=0
        missed=0
        for line in inf.readlines():
            line = line.split()
            if len(line)<1:
                continue
            word = line[0]
            try:
                if word in lines:
                    find += 1
                else:
                    missed+=1
                #print(word)
            except:
                missed += 1
            finally:
                print("find:"+str(find)+ " missed:" +str(missed))

    print("Find:" + str(find)+"  Missed:"+str(missed))
