f = open("\Users\eric.christiansen\Documents\Projects\case_00357631\log.txt")
for line in f:
   if "===" not in line: continue
   print line