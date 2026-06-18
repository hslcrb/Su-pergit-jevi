#!/bin/bash

echo "╔══════════════════════════════════════════════════════════╗"
echo "║         SuperGit-Jevi Web - Vercel 배포 스크립트         ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

# Vercel CLI 확인
if ! command -v vercel &> /dev/null
then
    echo "[에러] Vercel CLI가 설치되어 있지 않습니다."
    echo ""
    echo "다음 명령으로 설치하세요:"
    echo "  npm install -g vercel"
    echo ""
    exit 1
fi

# 현재 디렉토리 확인
if [ ! -f "package.json" ]; then
    echo "[에러] webapp 디렉토리에서 실행하세요."
    echo ""
    echo "올바른 위치:"
    echo "  cd webapp"
    echo "  ./deploy.sh"
    echo ""
    exit 1
fi

# 배포 타입 선택
echo "배포 타입을 선택하세요:"
echo "  [1] Preview (미리보기)"
echo "  [2] Production (프로덕션)"
echo ""
read -p "선택 (1 또는 2): " deploy_type

case $deploy_type in
    1)
        echo ""
        echo "[Preview 배포]"
        echo "미리보기 URL이 생성됩니다..."
        echo ""
        vercel
        ;;
    2)
        echo ""
        echo "[Production 배포]"
        echo "프로덕션 환경으로 배포합니다..."
        echo ""
        read -p "정말 프로덕션에 배포하시겠습니까? (y/N): " confirm
        
        if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
            vercel --prod
            echo ""
            echo "╔══════════════════════════════════════════════════════════╗"
            echo "║              🎉 배포 완료! 축하합니다!                   ║"
            echo "╚══════════════════════════════════════════════════════════╝"
        else
            echo ""
            echo "[취소됨] 배포가 취소되었습니다."
        fi
        ;;
    *)
        echo ""
        echo "[에러] 잘못된 선택입니다."
        exit 1
        ;;
esac

echo ""
