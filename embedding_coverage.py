from gensim.models import KeyedVectors
word_vectors = KeyedVectors.load_word2vec_format('/shared/preprocessed/yihaoc/word2vec/wiki.ru.vec')
print("vector read complete")
outlines = []

with open('/shared/corpora/ner/lorelei-swm-new/rus/column/rus.train','r',encoding='utf-8') as inf:
    find=0
    missed=0
    for line in inf.readlines():
        word = line.split()[0].strip()
        if len(word)<2:
            continue
        try:
            lst = word_vectors.most_similar(positive=[str(line)])
            find += 1
        except:
            missed += 1

    print("Find:" + str(find)+"  Missed:"+str(missed))