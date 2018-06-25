import deepcut

with open('/home/yihaoc/data/tha/lexicon/name_list.txt','rb') as f:
	lines = f.readlines();
outlines = []
for line in lines:
	words = deepcut.tokenize(line)
	s = ' '
	word = s.join(words)
	outlines.append(word)
for line in outlines:
	print line
with open('/home/yihaoc/data/tha/lexicon/py_split_name.txt','wb') as w:
	for line in outlines:
		w.write(line.encode('utf-8'))


