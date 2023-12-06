	.file	"nsieve.c"
	.option nopic
	.text
	.section	.rodata
	.align	3
.LC0:
	.string	"Primes up to %8u %8u\n"
	.text
	.align	1
	.type	nsieve, @function
nsieve:
	addi	sp,sp,-64
	sd	ra,56(sp)
	sd	s0,48(sp)
	addi	s0,sp,64
	mv	a5,a0
	sw	a5,-52(s0)
	sw	zero,-20(s0)
	lw	a5,-52(s0)
	mv	a0,a5
	call	malloc
	mv	a5,a0
	sd	a5,-40(s0)
	lw	a5,-52(s0)
	mv	a2,a5
	li	a1,1
	ld	a0,-40(s0)
	call	memset
	li	a5,2
	sw	a5,-24(s0)
	j	.L2
.L6:
	lwu	a5,-24(s0)
	ld	a4,-40(s0)
	add	a5,a4,a5
	lbu	a5,0(a5)
	beqz	a5,.L3
	lw	a5,-20(s0)
	addiw	a5,a5,1
	sw	a5,-20(s0)
	lw	a5,-24(s0)
	slliw	a5,a5,1
	sw	a5,-28(s0)
	j	.L4
.L5:
	lwu	a5,-28(s0)
	ld	a4,-40(s0)
	add	a5,a4,a5
	sb	zero,0(a5)
	lw	a4,-28(s0)
	lw	a5,-24(s0)
	addw	a5,a4,a5
	sw	a5,-28(s0)
.L4:
	lw	a4,-52(s0)
	lw	a5,-28(s0)
	sext.w	a5,a5
	bltu	a5,a4,.L5
.L3:
	lw	a5,-24(s0)
	addiw	a5,a5,1
	sw	a5,-24(s0)
.L2:
	lw	a4,-52(s0)
	lw	a5,-24(s0)
	sext.w	a5,a5
	bltu	a5,a4,.L6
	ld	a0,-40(s0)
	call	free
	lw	a4,-20(s0)
	lw	a5,-52(s0)
	mv	a2,a4
	mv	a1,a5
	lui	a5,%hi(.LC0)
	addi	a0,a5,%lo(.LC0)
	call	printf
	nop
	ld	ra,56(sp)
	ld	s0,48(sp)
	addi	sp,sp,64
	jr	ra
	.size	nsieve, .-nsieve
	.align	1
	.globl	main
	.type	main, @function
main:
	addi	sp,sp,-48
	sd	ra,40(sp)
	sd	s0,32(sp)
	addi	s0,sp,48
	mv	a5,a0
	sd	a1,-48(s0)
	sw	a5,-36(s0)
	ld	a5,-48(s0)
	addi	a5,a5,8
	ld	a5,0(a5)
	mv	a0,a5
	call	atoi
	mv	a5,a0
	sw	a5,-24(s0)
	sw	zero,-20(s0)
	j	.L8
.L9:
	lw	a4,-24(s0)
	lw	a5,-20(s0)
	subw	a5,a4,a5
	sext.w	a5,a5
	li	a4,8192
	addiw	a4,a4,1808
	sllw	a5,a4,a5
	sext.w	a5,a5
	mv	a0,a5
	call	nsieve
	lw	a5,-20(s0)
	addiw	a5,a5,1
	sw	a5,-20(s0)
.L8:
	lw	a5,-20(s0)
	sext.w	a4,a5
	li	a5,2
	ble	a4,a5,.L9
	li	a5,0
	mv	a0,a5
	ld	ra,40(sp)
	ld	s0,32(sp)
	addi	sp,sp,48
	jr	ra
	.size	main, .-main
	.ident	"GCC: (GNU) 8.1.0"
