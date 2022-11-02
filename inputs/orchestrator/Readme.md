
## Create archive from public repository 

```
git clone https://github.com/CS-SI/S2IPF_Orchestrator.git
git checkout 03.00.00
-
tar --exclude='S2IPF_Orchestrator/.git' --exclude='S2IPF_Orchestrator/.gitignore' -czvf orchestrator.tgz S2IPF_Orchestrator

