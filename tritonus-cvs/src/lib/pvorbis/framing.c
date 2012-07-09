/********************************************************************
 *                                                                  *
 * THIS FILE IS PART OF THE OggVorbis SOFTWARE CODEC SOURCE CODE.   *
 * USE, DISTRIBUTION AND REPRODUCTION OF THIS LIBRARY SOURCE IS     *
 * GOVERNED BY A BSD-STYLE SOURCE LICENSE INCLUDED WITH THIS SOURCE *
 * IN 'COPYING'. PLEASE READ THESE TERMS BEFORE DISTRIBUTING.       *
 *                                                                  *
 * THE OggVorbis SOURCE CODE IS (C) COPYRIGHT 1994-2002             *
 * by the Xiph.Org Foundation http://www.xiph.org/                  *
 *                                                                  *
 ********************************************************************

 function: code raw [Vorbis] packets into framed OggSquish stream and
           decode Ogg streams back into raw packets
 last mod: $Id: framing.c,v 1.5 2005/02/05 21:07:35 pfisterer Exp $

 note: The CRC code is directly derived from public domain code by
 Ross Williams (ross@guest.adelaide.edu.au).  See docs/framing.html
 for details.

 ********************************************************************/

#include <stdlib.h>
#include <string.h>
#include "ogg/ogg.h"

/* A complete description of Ogg framing exists in docs/framing.html */






static ogg_uint32_t crc_lookup[256]={
  0x00000000,0x04c11db7,0x09823b6e,0x0d4326d9,
  0x130476dc,0x17c56b6b,0x1a864db2,0x1e475005,
  0x2608edb8,0x22c9f00f,0x2f8ad6d6,0x2b4bcb61,
  0x350c9b64,0x31cd86d3,0x3c8ea00a,0x384fbdbd,
  0x4c11db70,0x48d0c6c7,0x4593e01e,0x4152fda9,
  0x5f15adac,0x5bd4b01b,0x569796c2,0x52568b75,
  0x6a1936c8,0x6ed82b7f,0x639b0da6,0x675a1011,
  0x791d4014,0x7ddc5da3,0x709f7b7a,0x745e66cd,
  0x9823b6e0,0x9ce2ab57,0x91a18d8e,0x95609039,
  0x8b27c03c,0x8fe6dd8b,0x82a5fb52,0x8664e6e5,
  0xbe2b5b58,0xbaea46ef,0xb7a96036,0xb3687d81,
  0xad2f2d84,0xa9ee3033,0xa4ad16ea,0xa06c0b5d,
  0xd4326d90,0xd0f37027,0xddb056fe,0xd9714b49,
  0xc7361b4c,0xc3f706fb,0xceb42022,0xca753d95,
  0xf23a8028,0xf6fb9d9f,0xfbb8bb46,0xff79a6f1,
  0xe13ef6f4,0xe5ffeb43,0xe8bccd9a,0xec7dd02d,
  0x34867077,0x30476dc0,0x3d044b19,0x39c556ae,
  0x278206ab,0x23431b1c,0x2e003dc5,0x2ac12072,
  0x128e9dcf,0x164f8078,0x1b0ca6a1,0x1fcdbb16,
  0x018aeb13,0x054bf6a4,0x0808d07d,0x0cc9cdca,
  0x7897ab07,0x7c56b6b0,0x71159069,0x75d48dde,
  0x6b93dddb,0x6f52c06c,0x6211e6b5,0x66d0fb02,
  0x5e9f46bf,0x5a5e5b08,0x571d7dd1,0x53dc6066,
  0x4d9b3063,0x495a2dd4,0x44190b0d,0x40d816ba,
  0xaca5c697,0xa864db20,0xa527fdf9,0xa1e6e04e,
  0xbfa1b04b,0xbb60adfc,0xb6238b25,0xb2e29692,
  0x8aad2b2f,0x8e6c3698,0x832f1041,0x87ee0df6,
  0x99a95df3,0x9d684044,0x902b669d,0x94ea7b2a,
  0xe0b41de7,0xe4750050,0xe9362689,0xedf73b3e,
  0xf3b06b3b,0xf771768c,0xfa325055,0xfef34de2,
  0xc6bcf05f,0xc27dede8,0xcf3ecb31,0xcbffd686,
  0xd5b88683,0xd1799b34,0xdc3abded,0xd8fba05a,
  0x690ce0ee,0x6dcdfd59,0x608edb80,0x644fc637,
  0x7a089632,0x7ec98b85,0x738aad5c,0x774bb0eb,
  0x4f040d56,0x4bc510e1,0x46863638,0x42472b8f,
  0x5c007b8a,0x58c1663d,0x558240e4,0x51435d53,
  0x251d3b9e,0x21dc2629,0x2c9f00f0,0x285e1d47,
  0x36194d42,0x32d850f5,0x3f9b762c,0x3b5a6b9b,
  0x0315d626,0x07d4cb91,0x0a97ed48,0x0e56f0ff,
  0x1011a0fa,0x14d0bd4d,0x19939b94,0x1d528623,
  0xf12f560e,0xf5ee4bb9,0xf8ad6d60,0xfc6c70d7,
  0xe22b20d2,0xe6ea3d65,0xeba91bbc,0xef68060b,
  0xd727bbb6,0xd3e6a601,0xdea580d8,0xda649d6f,
  0xc423cd6a,0xc0e2d0dd,0xcda1f604,0xc960ebb3,
  0xbd3e8d7e,0xb9ff90c9,0xb4bcb610,0xb07daba7,
  0xae3afba2,0xaafbe615,0xa7b8c0cc,0xa379dd7b,
  0x9b3660c6,0x9ff77d71,0x92b45ba8,0x9675461f,
  0x8832161a,0x8cf30bad,0x81b02d74,0x857130c3,
  0x5d8a9099,0x594b8d2e,0x5408abf7,0x50c9b640,
  0x4e8ee645,0x4a4ffbf2,0x470cdd2b,0x43cdc09c,
  0x7b827d21,0x7f436096,0x7200464f,0x76c15bf8,
  0x68860bfd,0x6c47164a,0x61043093,0x65c52d24,
  0x119b4be9,0x155a565e,0x18197087,0x1cd86d30,
  0x029f3d35,0x065e2082,0x0b1d065b,0x0fdc1bec,
  0x3793a651,0x3352bbe6,0x3e119d3f,0x3ad08088,
  0x2497d08d,0x2056cd3a,0x2d15ebe3,0x29d4f654,
  0xc5a92679,0xc1683bce,0xcc2b1d17,0xc8ea00a0,
  0xd6ad50a5,0xd26c4d12,0xdf2f6bcb,0xdbee767c,
  0xe3a1cbc1,0xe760d676,0xea23f0af,0xeee2ed18,
  0xf0a5bd1d,0xf464a0aa,0xf9278673,0xfde69bc4,
  0x89b8fd09,0x8d79e0be,0x803ac667,0x84fbdbd0,
  0x9abc8bd5,0x9e7d9662,0x933eb0bb,0x97ffad0c,
  0xafb010b1,0xab710d06,0xa6322bdf,0xa2f33668,
  0xbcb4666d,0xb8757bda,0xb5365d03,0xb1f740b4};





/* DECODING PRIMITIVES: packet streaming layer **********************/

/* This has two layers to place more of the multi-serialno and paging
   control in the application's hands.  First, we expose a data buffer
   using ogg_sync_buffer().  The app either copies into the
   buffer, or passes it directly to read(), etc.  We then call
   ogg_sync_wrote() to tell how many bytes we just added.

   Pages are returned (pointers into the buffer in ogg_sync_state)
   by ogg_sync_pageout().  The page is then submitted to
   ogg_stream_pagein() along with the appropriate
   ogg_stream_state* (ie, matching serialno).  We then get raw
   packets out calling ogg_stream_packetout() with a
   ogg_stream_state.  See the 'frame-prog.txt' docs for details and
   example code. */




void ogg_packet_clear(ogg_packet *op) {
  _ogg_free(op->packet);
  memset(op, 0, sizeof(*op));
}

#ifdef _V_SELFTEST
#include <stdio.h>

ogg_stream_state os_en, os_de;
ogg_sync_state oy;

void checkpacket(ogg_packet *op,int len, int no, int pos){
  long j;
  static int sequence=0;
  static int lastno=0;

  if(op->bytes!=len){
    fprintf(stderr,"incorrect packet length!\n");
    exit(1);
  }
  if(op->granulepos!=pos){
    fprintf(stderr,"incorrect packet position!\n");
    exit(1);
  }

  /* packet number just follows sequence/gap; adjust the input number
     for that */
  if(no==0){
    sequence=0;
  }else{
    sequence++;
    if(no>lastno+1)
      sequence++;
  }
  lastno=no;
  if(op->packetno!=sequence){
    fprintf(stderr,"incorrect packet sequence %ld != %d\n",
	    (long)(op->packetno),sequence);
    exit(1);
  }

  /* Test data */
  for(j=0;j<op->bytes;j++)
    if(op->packet[j]!=((j+no)&0xff)){
      fprintf(stderr,"body data mismatch (1) at pos %ld: %x!=%lx!\n\n",
	      j,op->packet[j],(j+no)&0xff);
      exit(1);
    }
}

void check_page(unsigned char *data,const int *header,ogg_page *og){
  long j;
  /* Test data */
  for(j=0;j<og->body_len;j++)
    if(og->body[j]!=data[j]){
      fprintf(stderr,"body data mismatch (2) at pos %ld: %x!=%x!\n\n",
	      j,data[j],og->body[j]);
      exit(1);
    }

  /* Test header */
  for(j=0;j<og->header_len;j++){
    if(og->header[j]!=header[j]){
      fprintf(stderr,"header content mismatch at pos %ld:\n",j);
      for(j=0;j<header[26]+27;j++)
	fprintf(stderr," (%ld)%02x:%02x",j,header[j],og->header[j]);
      fprintf(stderr,"\n");
      exit(1);
    }
  }
  if(og->header_len!=header[26]+27){
    fprintf(stderr,"header length incorrect! (%ld!=%d)\n",
	    og->header_len,header[26]+27);
    exit(1);
  }
}

void print_header(ogg_page *og){
  int j;
  fprintf(stderr,"\nHEADER:\n");
  fprintf(stderr,"  capture: %c %c %c %c  version: %d  flags: %x\n",
	  og->header[0],og->header[1],og->header[2],og->header[3],
	  (int)og->header[4],(int)og->header[5]);

  fprintf(stderr,"  granulepos: %d  serialno: %d  pageno: %ld\n",
	  (og->header[9]<<24)|(og->header[8]<<16)|
	  (og->header[7]<<8)|og->header[6],
	  (og->header[17]<<24)|(og->header[16]<<16)|
	  (og->header[15]<<8)|og->header[14],
	  ((long)(og->header[21])<<24)|(og->header[20]<<16)|
	  (og->header[19]<<8)|og->header[18]);

  fprintf(stderr,"  checksum: %02x:%02x:%02x:%02x\n  segments: %d (",
	  (int)og->header[22],(int)og->header[23],
	  (int)og->header[24],(int)og->header[25],
	  (int)og->header[26]);

  for(j=27;j<og->header_len;j++)
    fprintf(stderr,"%d ",(int)og->header[j]);
  fprintf(stderr,")\n\n");
}

void copy_page(ogg_page *og){
  unsigned char *temp=_ogg_malloc(og->header_len);
  memcpy(temp,og->header,og->header_len);
  og->header=temp;

  temp=_ogg_malloc(og->body_len);
  memcpy(temp,og->body,og->body_len);
  og->body=temp;
}

void error(void){
  fprintf(stderr,"error!\n");
  exit(1);
}

/* 17 only */
const int head1_0[] = {0x4f,0x67,0x67,0x53,0,0x06,
		       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,0,0,0,0,
		       0x15,0xed,0xec,0x91,
		       1,
		       17};

/* 17, 254, 255, 256, 500, 510, 600 byte, pad */
const int head1_1[] = {0x4f,0x67,0x67,0x53,0,0x02,
		       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,0,0,0,0,
		       0x59,0x10,0x6c,0x2c,
		       1,
		       17};
const int head2_1[] = {0x4f,0x67,0x67,0x53,0,0x04,
		       0x07,0x18,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,1,0,0,0,
		       0x89,0x33,0x85,0xce,
		       13,
		       254,255,0,255,1,255,245,255,255,0,
		       255,255,90};

/* nil packets; beginning,middle,end */
const int head1_2[] = {0x4f,0x67,0x67,0x53,0,0x02,
		       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,0,0,0,0,
		       0xff,0x7b,0x23,0x17,
		       1,
		       0};
const int head2_2[] = {0x4f,0x67,0x67,0x53,0,0x04,
		       0x07,0x28,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,1,0,0,0,
		       0x5c,0x3f,0x66,0xcb,
		       17,
		       17,254,255,0,0,255,1,0,255,245,255,255,0,
		       255,255,90,0};

/* large initial packet */
const int head1_3[] = {0x4f,0x67,0x67,0x53,0,0x02,
		       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,0,0,0,0,
		       0x01,0x27,0x31,0xaa,
		       18,
		       255,255,255,255,255,255,255,255,
		       255,255,255,255,255,255,255,255,255,10};

const int head2_3[] = {0x4f,0x67,0x67,0x53,0,0x04,
		       0x07,0x08,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,1,0,0,0,
		       0x7f,0x4e,0x8a,0xd2,
		       4,
		       255,4,255,0};


/* continuing packet test */
const int head1_4[] = {0x4f,0x67,0x67,0x53,0,0x02,
		       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,0,0,0,0,
		       0xff,0x7b,0x23,0x17,
		       1,
		       0};

const int head2_4[] = {0x4f,0x67,0x67,0x53,0,0x00,
		       0x07,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,1,0,0,0,
		       0x34,0x24,0xd5,0x29,
		       17,
		       255,255,255,255,255,255,255,255,
		       255,255,255,255,255,255,255,255,255};

const int head3_4[] = {0x4f,0x67,0x67,0x53,0,0x05,
		       0x07,0x0c,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,2,0,0,0,
		       0xc8,0xc3,0xcb,0xed,
		       5,
		       10,255,4,255,0};


/* page with the 255 segment limit */
const int head1_5[] = {0x4f,0x67,0x67,0x53,0,0x02,
		       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,0,0,0,0,
		       0xff,0x7b,0x23,0x17,
		       1,
		       0};

const int head2_5[] = {0x4f,0x67,0x67,0x53,0,0x00,
		       0x07,0xfc,0x03,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,1,0,0,0,
		       0xed,0x2a,0x2e,0xa7,
		       255,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10,10,
		       10,10,10,10,10,10,10};

const int head3_5[] = {0x4f,0x67,0x67,0x53,0,0x04,
		       0x07,0x00,0x04,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,2,0,0,0,
		       0x6c,0x3b,0x82,0x3d,
		       1,
		       50};


/* packet that overspans over an entire page */
const int head1_6[] = {0x4f,0x67,0x67,0x53,0,0x02,
		       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,0,0,0,0,
		       0xff,0x7b,0x23,0x17,
		       1,
		       0};

const int head2_6[] = {0x4f,0x67,0x67,0x53,0,0x00,
		       0x07,0x04,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,1,0,0,0,
		       0x3c,0xd9,0x4d,0x3f,
		       17,
		       100,255,255,255,255,255,255,255,255,
		       255,255,255,255,255,255,255,255};

const int head3_6[] = {0x4f,0x67,0x67,0x53,0,0x01,
		       0x07,0x04,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,2,0,0,0,
		       0xbd,0xd5,0xb5,0x8b,
		       17,
		       255,255,255,255,255,255,255,255,
		       255,255,255,255,255,255,255,255,255};

const int head4_6[] = {0x4f,0x67,0x67,0x53,0,0x05,
		       0x07,0x10,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,3,0,0,0,
		       0xef,0xdd,0x88,0xde,
		       7,
		       255,255,75,255,4,255,0};

/* packet that overspans over an entire page */
const int head1_7[] = {0x4f,0x67,0x67,0x53,0,0x02,
		       0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,0,0,0,0,
		       0xff,0x7b,0x23,0x17,
		       1,
		       0};

const int head2_7[] = {0x4f,0x67,0x67,0x53,0,0x00,
		       0x07,0x04,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,1,0,0,0,
		       0x3c,0xd9,0x4d,0x3f,
		       17,
		       100,255,255,255,255,255,255,255,255,
		       255,255,255,255,255,255,255,255};

const int head3_7[] = {0x4f,0x67,0x67,0x53,0,0x05,
		       0x07,0x08,0x00,0x00,0x00,0x00,0x00,0x00,
		       0x01,0x02,0x03,0x04,2,0,0,0,
		       0xd4,0xe0,0x60,0xe5,
		       1,0};

void test_pack(const int *pl, const int **headers){
  unsigned char *data=_ogg_malloc(1024*1024); /* for scripted test cases only */
  long inptr=0;
  long outptr=0;
  long deptr=0;
  long depacket=0;
  long granule_pos=7,pageno=0;
  int i,j,packets,pageout=0;
  int eosflag=0;
  int bosflag=0;

  ogg_stream_reset(&os_en);
  ogg_stream_reset(&os_de);
  ogg_sync_reset(&oy);

  for(packets=0;;packets++)if(pl[packets]==-1)break;

  for(i=0;i<packets;i++){
    /* construct a test packet */
    ogg_packet op;
    int len=pl[i];
    
    op.packet=data+inptr;
    op.bytes=len;
    op.e_o_s=(pl[i+1]<0?1:0);
    op.granulepos=granule_pos;

    granule_pos+=1024;

    for(j=0;j<len;j++)data[inptr++]=i+j;

    /* submit the test packet */
    ogg_stream_packetin(&os_en,&op);

    /* retrieve any finished pages */
    {
      ogg_page og;
      
      while(ogg_stream_pageout(&os_en,&og)){
	/* We have a page.  Check it carefully */

	fprintf(stderr,"%ld, ",pageno);

	if(headers[pageno]==NULL){
	  fprintf(stderr,"coded too many pages!\n");
	  exit(1);
	}

	check_page(data+outptr,headers[pageno],&og);

	outptr+=og.body_len;
	pageno++;

	/* have a complete page; submit it to sync/decode */

	{
	  ogg_page og_de;
	  ogg_packet op_de,op_de2;
	  char *buf=ogg_sync_buffer(&oy,og.header_len+og.body_len);
	  memcpy(buf,og.header,og.header_len);
	  memcpy(buf+og.header_len,og.body,og.body_len);
	  ogg_sync_wrote(&oy,og.header_len+og.body_len);

	  while(ogg_sync_pageout(&oy,&og_de)>0){
	    /* got a page.  Happy happy.  Verify that it's good. */
	    
	    check_page(data+deptr,headers[pageout],&og_de);
	    deptr+=og_de.body_len;
	    pageout++;

	    /* submit it to deconstitution */
	    ogg_stream_pagein(&os_de,&og_de);

	    /* packets out? */
	    while(ogg_stream_packetpeek(&os_de,&op_de2)>0){
	      ogg_stream_packetpeek(&os_de,NULL);
	      ogg_stream_packetout(&os_de,&op_de); /* just catching them all */
	      
	      /* verify peek and out match */
	      if(memcmp(&op_de,&op_de2,sizeof(op_de))){
		fprintf(stderr,"packetout != packetpeek! pos=%ld\n",
			depacket);
		exit(1);
	      }

	      /* verify the packet! */
	      /* check data */
	      if(memcmp(data+depacket,op_de.packet,op_de.bytes)){
		fprintf(stderr,"packet data mismatch in decode! pos=%ld\n",
			depacket);
		exit(1);
	      }
	      /* check bos flag */
	      if(bosflag==0 && op_de.b_o_s==0){
		fprintf(stderr,"b_o_s flag not set on packet!\n");
		exit(1);
	      }
	      if(bosflag && op_de.b_o_s){
		fprintf(stderr,"b_o_s flag incorrectly set on packet!\n");
		exit(1);
	      }
	      bosflag=1;
	      depacket+=op_de.bytes;
	      
	      /* check eos flag */
	      if(eosflag){
		fprintf(stderr,"Multiple decoded packets with eos flag!\n");
		exit(1);
	      }

	      if(op_de.e_o_s)eosflag=1;

	      /* check granulepos flag */
	      if(op_de.granulepos!=-1){
		fprintf(stderr," granule:%ld ",(long)op_de.granulepos);
	      }
	    }
	  }
	}
      }
    }
  }
  _ogg_free(data);
  if(headers[pageno]!=NULL){
    fprintf(stderr,"did not write last page!\n");
    exit(1);
  }
  if(headers[pageout]!=NULL){
    fprintf(stderr,"did not decode last page!\n");
    exit(1);
  }
  if(inptr!=outptr){
    fprintf(stderr,"encoded page data incomplete!\n");
    exit(1);
  }
  if(inptr!=deptr){
    fprintf(stderr,"decoded page data incomplete!\n");
    exit(1);
  }
  if(inptr!=depacket){
    fprintf(stderr,"decoded packet data incomplete!\n");
    exit(1);
  }
  if(!eosflag){
    fprintf(stderr,"Never got a packet with EOS set!\n");
    exit(1);
  }
  fprintf(stderr,"ok.\n");
}

int main(void){

  ogg_stream_init(&os_en,0x04030201);
  ogg_stream_init(&os_de,0x04030201);
  ogg_sync_init(&oy);

  /* Exercise each code path in the framing code.  Also verify that
     the checksums are working.  */

  {
    /* 17 only */
    const int packets[]={17, -1};
    const int *headret[]={head1_0,NULL};
    
    fprintf(stderr,"testing single page encoding... ");
    test_pack(packets,headret);
  }

  {
    /* 17, 254, 255, 256, 500, 510, 600 byte, pad */
    const int packets[]={17, 254, 255, 256, 500, 510, 600, -1};
    const int *headret[]={head1_1,head2_1,NULL};
    
    fprintf(stderr,"testing basic page encoding... ");
    test_pack(packets,headret);
  }

  {
    /* nil packets; beginning,middle,end */
    const int packets[]={0,17, 254, 255, 0, 256, 0, 500, 510, 600, 0, -1};
    const int *headret[]={head1_2,head2_2,NULL};
    
    fprintf(stderr,"testing basic nil packets... ");
    test_pack(packets,headret);
  }

  {
    /* large initial packet */
    const int packets[]={4345,259,255,-1};
    const int *headret[]={head1_3,head2_3,NULL};
    
    fprintf(stderr,"testing initial-packet lacing > 4k... ");
    test_pack(packets,headret);
  }

  {
    /* continuing packet test */
    const int packets[]={0,4345,259,255,-1};
    const int *headret[]={head1_4,head2_4,head3_4,NULL};
    
    fprintf(stderr,"testing single packet page span... ");
    test_pack(packets,headret);
  }

  /* page with the 255 segment limit */
  {

    const int packets[]={0,10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,10,
		   10,10,10,10,10,10,10,50,-1};
    const int *headret[]={head1_5,head2_5,head3_5,NULL};
    
    fprintf(stderr,"testing max packet segments... ");
    test_pack(packets,headret);
  }

  {
    /* packet that overspans over an entire page */
    const int packets[]={0,100,9000,259,255,-1};
    const int *headret[]={head1_6,head2_6,head3_6,head4_6,NULL};
    
    fprintf(stderr,"testing very large packets... ");
    test_pack(packets,headret);
  }

  {
    /* term only page.  why not? */
    const int packets[]={0,100,4080,-1};
    const int *headret[]={head1_7,head2_7,head3_7,NULL};
    
    fprintf(stderr,"testing zero data page (1 nil packet)... ");
    test_pack(packets,headret);
  }



  {
    /* build a bunch of pages for testing */
    unsigned char *data=_ogg_malloc(1024*1024);
    int pl[]={0,100,4079,2956,2057,76,34,912,0,234,1000,1000,1000,300,-1};
    int inptr=0,i,j;
    ogg_page og[5];
    
    ogg_stream_reset(&os_en);

    for(i=0;pl[i]!=-1;i++){
      ogg_packet op;
      int len=pl[i];
      
      op.packet=data+inptr;
      op.bytes=len;
      op.e_o_s=(pl[i+1]<0?1:0);
      op.granulepos=(i+1)*1000;

      for(j=0;j<len;j++)data[inptr++]=i+j;
      ogg_stream_packetin(&os_en,&op);
    }

    _ogg_free(data);

    /* retrieve finished pages */
    for(i=0;i<5;i++){
      if(ogg_stream_pageout(&os_en,&og[i])==0){
	fprintf(stderr,"Too few pages output building sync tests!\n");
	exit(1);
      }
      copy_page(&og[i]);
    }

    /* Test lost pages on pagein/packetout: no rollback */
    {
      ogg_page temp;
      ogg_packet test;

      fprintf(stderr,"Testing loss of pages... ");

      ogg_sync_reset(&oy);
      ogg_stream_reset(&os_de);
      for(i=0;i<5;i++){
	memcpy(ogg_sync_buffer(&oy,og[i].header_len),og[i].header,
	       og[i].header_len);
	ogg_sync_wrote(&oy,og[i].header_len);
	memcpy(ogg_sync_buffer(&oy,og[i].body_len),og[i].body,og[i].body_len);
	ogg_sync_wrote(&oy,og[i].body_len);
      }

      ogg_sync_pageout(&oy,&temp);
      ogg_stream_pagein(&os_de,&temp);
      ogg_sync_pageout(&oy,&temp);
      ogg_stream_pagein(&os_de,&temp);
      ogg_sync_pageout(&oy,&temp);
      /* skip */
      ogg_sync_pageout(&oy,&temp);
      ogg_stream_pagein(&os_de,&temp);

      /* do we get the expected results/packets? */
      
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,0,0,0);
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,100,1,-1);
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,4079,2,3000);
      if(ogg_stream_packetout(&os_de,&test)!=-1){
	fprintf(stderr,"Error: loss of page did not return error\n");
	exit(1);
      }
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,76,5,-1);
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,34,6,-1);
      fprintf(stderr,"ok.\n");
    }

    /* Test lost pages on pagein/packetout: rollback with continuation */
    {
      ogg_page temp;
      ogg_packet test;

      fprintf(stderr,"Testing loss of pages (rollback required)... ");

      ogg_sync_reset(&oy);
      ogg_stream_reset(&os_de);
      for(i=0;i<5;i++){
	memcpy(ogg_sync_buffer(&oy,og[i].header_len),og[i].header,
	       og[i].header_len);
	ogg_sync_wrote(&oy,og[i].header_len);
	memcpy(ogg_sync_buffer(&oy,og[i].body_len),og[i].body,og[i].body_len);
	ogg_sync_wrote(&oy,og[i].body_len);
      }

      ogg_sync_pageout(&oy,&temp);
      ogg_stream_pagein(&os_de,&temp);
      ogg_sync_pageout(&oy,&temp);
      ogg_stream_pagein(&os_de,&temp);
      ogg_sync_pageout(&oy,&temp);
      ogg_stream_pagein(&os_de,&temp);
      ogg_sync_pageout(&oy,&temp);
      /* skip */
      ogg_sync_pageout(&oy,&temp);
      ogg_stream_pagein(&os_de,&temp);

      /* do we get the expected results/packets? */
      
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,0,0,0);
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,100,1,-1);
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,4079,2,3000);
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,2956,3,4000);
      if(ogg_stream_packetout(&os_de,&test)!=-1){
	fprintf(stderr,"Error: loss of page did not return error\n");
	exit(1);
      }
      if(ogg_stream_packetout(&os_de,&test)!=1)error();
      checkpacket(&test,300,13,14000);
      fprintf(stderr,"ok.\n");
    }
    
    /* the rest only test sync */
    {
      ogg_page og_de;
      /* Test fractional page inputs: incomplete capture */
      fprintf(stderr,"Testing sync on partial inputs... ");
      ogg_sync_reset(&oy);
      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header,
	     3);
      ogg_sync_wrote(&oy,3);
      if(ogg_sync_pageout(&oy,&og_de)>0)error();
      
      /* Test fractional page inputs: incomplete fixed header */
      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header+3,
	     20);
      ogg_sync_wrote(&oy,20);
      if(ogg_sync_pageout(&oy,&og_de)>0)error();
      
      /* Test fractional page inputs: incomplete header */
      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header+23,
	     5);
      ogg_sync_wrote(&oy,5);
      if(ogg_sync_pageout(&oy,&og_de)>0)error();
      
      /* Test fractional page inputs: incomplete body */
      
      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header+28,
	     og[1].header_len-28);
      ogg_sync_wrote(&oy,og[1].header_len-28);
      if(ogg_sync_pageout(&oy,&og_de)>0)error();
      
      memcpy(ogg_sync_buffer(&oy,og[1].body_len),og[1].body,1000);
      ogg_sync_wrote(&oy,1000);
      if(ogg_sync_pageout(&oy,&og_de)>0)error();
      
      memcpy(ogg_sync_buffer(&oy,og[1].body_len),og[1].body+1000,
	     og[1].body_len-1000);
      ogg_sync_wrote(&oy,og[1].body_len-1000);
      if(ogg_sync_pageout(&oy,&og_de)<=0)error();
      
      fprintf(stderr,"ok.\n");
    }

    /* Test fractional page inputs: page + incomplete capture */
    {
      ogg_page og_de;
      fprintf(stderr,"Testing sync on 1+partial inputs... ");
      ogg_sync_reset(&oy); 

      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header,
	     og[1].header_len);
      ogg_sync_wrote(&oy,og[1].header_len);

      memcpy(ogg_sync_buffer(&oy,og[1].body_len),og[1].body,
	     og[1].body_len);
      ogg_sync_wrote(&oy,og[1].body_len);

      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header,
	     20);
      ogg_sync_wrote(&oy,20);
      if(ogg_sync_pageout(&oy,&og_de)<=0)error();
      if(ogg_sync_pageout(&oy,&og_de)>0)error();

      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header+20,
	     og[1].header_len-20);
      ogg_sync_wrote(&oy,og[1].header_len-20);
      memcpy(ogg_sync_buffer(&oy,og[1].body_len),og[1].body,
	     og[1].body_len);
      ogg_sync_wrote(&oy,og[1].body_len);
      if(ogg_sync_pageout(&oy,&og_de)<=0)error();

      fprintf(stderr,"ok.\n");
    }
    
    /* Test recapture: garbage + page */
    {
      ogg_page og_de;
      fprintf(stderr,"Testing search for capture... ");
      ogg_sync_reset(&oy); 
      
      /* 'garbage' */
      memcpy(ogg_sync_buffer(&oy,og[1].body_len),og[1].body,
	     og[1].body_len);
      ogg_sync_wrote(&oy,og[1].body_len);

      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header,
	     og[1].header_len);
      ogg_sync_wrote(&oy,og[1].header_len);

      memcpy(ogg_sync_buffer(&oy,og[1].body_len),og[1].body,
	     og[1].body_len);
      ogg_sync_wrote(&oy,og[1].body_len);

      memcpy(ogg_sync_buffer(&oy,og[2].header_len),og[2].header,
	     20);
      ogg_sync_wrote(&oy,20);
      if(ogg_sync_pageout(&oy,&og_de)>0)error();
      if(ogg_sync_pageout(&oy,&og_de)<=0)error();
      if(ogg_sync_pageout(&oy,&og_de)>0)error();

      memcpy(ogg_sync_buffer(&oy,og[2].header_len),og[2].header+20,
	     og[2].header_len-20);
      ogg_sync_wrote(&oy,og[2].header_len-20);
      memcpy(ogg_sync_buffer(&oy,og[2].body_len),og[2].body,
	     og[2].body_len);
      ogg_sync_wrote(&oy,og[2].body_len);
      if(ogg_sync_pageout(&oy,&og_de)<=0)error();

      fprintf(stderr,"ok.\n");
    }

    /* Test recapture: page + garbage + page */
    {
      ogg_page og_de;
      fprintf(stderr,"Testing recapture... ");
      ogg_sync_reset(&oy); 

      memcpy(ogg_sync_buffer(&oy,og[1].header_len),og[1].header,
	     og[1].header_len);
      ogg_sync_wrote(&oy,og[1].header_len);

      memcpy(ogg_sync_buffer(&oy,og[1].body_len),og[1].body,
	     og[1].body_len);
      ogg_sync_wrote(&oy,og[1].body_len);

      memcpy(ogg_sync_buffer(&oy,og[2].header_len),og[2].header,
	     og[2].header_len);
      ogg_sync_wrote(&oy,og[2].header_len);

      memcpy(ogg_sync_buffer(&oy,og[2].header_len),og[2].header,
	     og[2].header_len);
      ogg_sync_wrote(&oy,og[2].header_len);

      if(ogg_sync_pageout(&oy,&og_de)<=0)error();

      memcpy(ogg_sync_buffer(&oy,og[2].body_len),og[2].body,
	     og[2].body_len-5);
      ogg_sync_wrote(&oy,og[2].body_len-5);

      memcpy(ogg_sync_buffer(&oy,og[3].header_len),og[3].header,
	     og[3].header_len);
      ogg_sync_wrote(&oy,og[3].header_len);

      memcpy(ogg_sync_buffer(&oy,og[3].body_len),og[3].body,
	     og[3].body_len);
      ogg_sync_wrote(&oy,og[3].body_len);

      if(ogg_sync_pageout(&oy,&og_de)>0)error();
      if(ogg_sync_pageout(&oy,&og_de)<=0)error();

      fprintf(stderr,"ok.\n");
    }
  }    

  return(0);
}

#endif




