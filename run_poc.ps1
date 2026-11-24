# Roda testes + cobertura + análise do SonarQube e falha se o Quality Gate reprovar.
# Uso:  .\run_poc.ps1   (token no arquivo .env ou em $env:SONAR_TOKEN)

# Carrega variáveis do arquivo .env, se existir
if (Test-Path .env) {
    Get-Content .env | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]*)=(.*)$') {
            Set-Item -Path "env:$($matches[1].Trim())" -Value $matches[2].Trim()
        }
    }
}

if (-not $env:SONAR_TOKEN) {
    Write-Host "Token não encontrado. Crie o arquivo .env com a linha SONAR_TOKEN=squ_..." -ForegroundColor Red
    exit 1
}

# Confere se o SonarQube está no ar antes de começar
try {
    $status = (Invoke-RestMethod -Uri "http://localhost:9000/api/system/status" -TimeoutSec 5).status
} catch {
    $status = "FORA DO AR"
}
if ($status -ne "UP") {
    Write-Host "SonarQube não está pronto (status: $status)." -ForegroundColor Red
    Write-Host "Suba o SonarQube na pasta da POC anterior com: docker compose up -d" -ForegroundColor Yellow
    exit 1
}

Write-Host "`nRodando testes, cobertura e análise no SonarQube..." -ForegroundColor Cyan
Write-Host "(a primeira execução baixa as dependências do Maven e pode levar alguns minutos)`n"
docker compose run --rm analise
if ($LASTEXITCODE -ne 0) {
    Write-Host "`nQUALITY GATE REPROVADO (ou falha nos testes) - veja http://localhost:9000/dashboard?id=POC-Catalogo-Impressoras" -ForegroundColor Red
    exit 1
}

Write-Host "`nQUALITY GATE APROVADO" -ForegroundColor Green
