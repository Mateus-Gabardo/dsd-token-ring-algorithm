# Token Ring Algorithm

Este projeto implementa o algoritmo Token Ring. O algoritmo Token Ring é um protocolo de comunicação que permite que os participantes de um sistema distribuído tenham a oportunidade de acessar uma seção crítica de forma sequencial, passando um token entre eles.

## Conceito

No algoritmo Token Ring, os participantes são organizados em um anel lógico. Cada participante aguarda a posse do token para entrar na seção crítica, onde executa uma tarefa específica. Após a conclusão da tarefa, o participante passa o token para o próximo participante no anel, permitindo que ele acesse a seção crítica.

A passagem do token garante que cada participante tenha uma oportunidade justa de acessar a seção crítica, evitando conflitos e garantindo a ordem de execução.

## Funcionamento

Neste projeto, o algoritmo Token Ring é implementado em um jogo de adivinhação de números. Os participantes são os jogadores que se conectam ao servidor e tentam adivinhar um número secreto. Eles passam o token entre si, permitindo que cada jogador tenha a oportunidade de fazer um palpite e verificar se está correto.

O servidor inicia o jogo enviando o token para o primeiro jogador na lista. Quando um jogador recebe o token, ele tem a chance de fazer um palpite. Em seguida, o jogador passa o token para o próximo jogador no anel, para que ele possa fazer seu palpite.

O processo continua até que um jogador acerte o número secreto ou até que o jogo seja encerrado por algum motivo.

## Como executar o projeto

1. Clone este repositório em seu ambiente local.

2. Navegue para o diretório raiz do projeto.

3. Compile as classes Java do projeto:

