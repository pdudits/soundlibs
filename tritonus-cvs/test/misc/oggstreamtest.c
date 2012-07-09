/*
 * oggstreamtest.c
 */

#include <stdio.h>
#include <ogg/ogg.h>


void
output_page(ogg_page* page)
{
	printf("Page:\n");
	printf("  version: %d\n", ogg_page_version(page));
	printf("  continued: %d\n", ogg_page_continued(page));
	printf("  bos: %d\n", ogg_page_bos(page));
	printf("  eos: %d\n", ogg_page_eos(page));
	printf("  granule position: %d\n", ogg_page_granulepos(page));
	printf("  stream serial number: %d\n", ogg_page_serialno(page));
	printf("  page sequence number: %d\n", ogg_page_pageno(page));
}



void
read_from_stream(ogg_sync_state* sync_state)
{
	char*	buffer;
	int	bytes;

	buffer = ogg_sync_buffer(sync_state, 4096);
	bytes = fread(buffer, 1, 4096, stdin);
	ogg_sync_wrote(sync_state, bytes);
}



void
output_packet(ogg_packet* packet)
{
	printf("    Packet: ");
	printf("bos: %d ", packet->b_o_s);
	printf("eos: %d ", packet->e_o_s);
	printf("granule position: %d\n", packet->granulepos);
	printf("      packet number: %d ", packet->packetno);
	printf("data length: %d\n", packet->bytes);
}



void
process_packets(ogg_stream_state* stream_state)
{
	int		nResult;
	ogg_packet	packet;

	while (1)
	{
		nResult = ogg_stream_packetout(stream_state, &packet);
		if (nResult == 0)
		{
			return;
		}
		else if (nResult == -1)
		{
			printf("corrupt data; can't get packet\n");
		}
		else
		{
			output_packet(&packet);
		}
	}
}


void
process_pages(ogg_sync_state* sync_state,
	      ogg_stream_state* stream_state)
{
	int		nResult;
	ogg_page	page;

	while (1)
	{
		nResult = ogg_sync_pageout(sync_state, &page);
		if (nResult == 0)
		{
			// need more data
			return;
		}
		else if (nResult == -1)
		{
			printf("sync lost; can't get page\n");
		}
		else
		{
			output_page(&page);
			nResult = ogg_stream_pagein(stream_state, &page);
			if (nResult == -1)
			{
				printf("can't add page to stream");
			}
			process_packets(stream_state);
		}
	}
}



main()
{
	ogg_sync_state		sync_state;
	ogg_stream_state	stream_state;
	ogg_page		page;

	int	nResult;

	ogg_sync_init(&sync_state);
	read_from_stream(&sync_state);
	ogg_sync_pageout(&sync_state, &page);
	output_page(&page);
	ogg_stream_init(&stream_state, ogg_page_serialno(&page));
	nResult = ogg_stream_pagein(&stream_state, &page);
	if (nResult == -1)
	{
		printf("can't add page to stream");
	}
	process_packets(&stream_state);
	while (1)
	{
		process_pages(&sync_state, &stream_state);
		read_from_stream(&sync_state);
	}
}



/*** oggstreamtest.c ***/
