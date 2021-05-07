/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api;

import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.Result;
import com.sportradar.mts.sdk.api.impl.mtsdto.ticketresponse.TicketResponseSchema;
import com.sportradar.mts.sdk.api.utils.*;
import com.sportradar.mts.sdk.impl.libs.TimeLimitedTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

@SuppressWarnings("SpellCheckingInspection")
public class TicketMapperTest  extends TimeLimitedTestBase {

    private final String DirPath = Paths.get("").toString() + "/json";
    private TicketBuilderHelper ticketBuilderHelper;

    @Before
    public void Init()
    {
        ticketBuilderHelper = new TicketBuilderHelper(null);
    }
//
//    @Test
//    public void BuildTicketDtoFromTicketTest() {
//        Ticket ticket = TicketBuilderHelper.GetTicket();
//        TicketDTO dto = new TicketMapper().Map(ticket);
//        String json = dto.ToJson();
//
//        var newDto = new TicketDTO {
//            Ticket = Ticket.FromJson(json)
//        } ;
//
//        TicketCompareHelper.Compare(ticket, dto);
//        TicketCompareHelper.Compare(ticket, newDto);
//    }

    @Test
    public void ValidateTicketIdPatternTest()
    {
        Assert.assertTrue(MtsTicketHelper.validateTicketId("a"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("aAB"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("129837403"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("AaZYc"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("ticket:1"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("Test_12123"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("T:123_434"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("T::23123"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("::__r"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("-"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("Test-3423-324234-2343243"));
        Assert.assertTrue(MtsTicketHelper.validateTicketId("B0034827552620261"));
    }

    @Test
    public void ValidateUserIdPatternTest()
    {
        Assert.assertTrue(MtsTicketHelper.validateUserId("a"));
        Assert.assertTrue(MtsTicketHelper.validateUserId("aAB"));
        Assert.assertTrue(MtsTicketHelper.validateUserId("129837403"));
        Assert.assertTrue(MtsTicketHelper.validateUserId("AaZYc"));
        Assert.assertFalse(MtsTicketHelper.validateUserId("ticket:1"));
        Assert.assertTrue(MtsTicketHelper.validateUserId("Test_12123"));
        Assert.assertFalse(MtsTicketHelper.validateUserId("T:123_434"));
        Assert.assertFalse(MtsTicketHelper.validateUserId("T::23123"));
        Assert.assertFalse(MtsTicketHelper.validateUserId("::__r"));
        Assert.assertTrue(MtsTicketHelper.validateUserId("-"));
        Assert.assertTrue(MtsTicketHelper.validateUserId("Test-3423-324234-2343243"));
        Assert.assertTrue(MtsTicketHelper.validateUserId("B0034827552620261"));
    }

    @Test
    public void ParseTicketWithNormalTicketIdTest()
    {
        Ticket ticket = ticketBuilderHelper.getTicket(null, 0, 0, 0);
        Assert.assertNotNull(ticket);
    }

    @Test
    public void BuildTicketResponseDtoFromJsonTest()
    {
        TicketResponseSchema dto = GetTicketResponseSchema("json/ticket-response.json");
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getResult());
        Assert.assertEquals(Result.Status.ACCEPTED, dto.getResult().getStatus());
        Assert.assertNotNull(dto.getResult().getReason());
        Assert.assertEquals(Integer.valueOf(1024), dto.getResult().getReason().getCode());
        Assert.assertEquals(SdkInfo.MTS_TICKET_VERSION, dto.getVersion());
    }

    @Test
    public void BuildTicketResponseDtoFromJson2Test()
    {
        TicketResponseSchema dto = GetTicketResponseSchema("json/ticket-response2.json");
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getResult());
        Assert.assertEquals(Result.Status.REJECTED, dto.getResult().getStatus());
        Assert.assertNotNull(dto.getResult().getReason());
        Assert.assertEquals(Integer.valueOf(-422), dto.getResult().getReason().getCode());
        Assert.assertEquals(SdkInfo.MTS_TICKET_VERSION, dto.getVersion());
    }

//    @Test
//    public void BuildTicketResponseFromTicketResponseDtoTest()
//    {
//        TicketResponseSchema dto = GetTicketResponseSchema("json/ticket-response2.json");
//        TicketResponse ticket = new TicketResponseMapper(null).Map(dto);
//        TicketCompareHelper.Compare(ticket, dto);
//    }

    @Test
    public void BuildTicketResponseDtoFromJson3_EmptySelectionDetailsTest()
    {
        TicketResponseSchema dto = GetTicketResponseSchema("json/ticket-response3.json");
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getResult());
        Assert.assertEquals(Result.Status.REJECTED, dto.getResult().getStatus());
        Assert.assertNotNull(dto.getResult().getReason());
        Assert.assertEquals(Integer.valueOf(-321), dto.getResult().getReason().getCode());
        Assert.assertEquals(SdkInfo.MTS_TICKET_VERSION, dto.getVersion());
        Assert.assertNotNull(dto.getResult().getBetDetails());
        Assert.assertNotNull(dto.getResult().getBetDetails().stream().findFirst().get().getSelectionDetails());
        Assert.assertEquals(0, dto.getResult().getBetDetails().stream().findFirst().get().getSelectionDetails().size());
    }

//    @Test
//    public void BuildTicketResponseFromTicketResponseDto_EmptySelectionDetailsTest()
//    {
//        TicketResponseSchema dto = GetTicketResponseSchema("json/ticket-response3.json");
//        TicketResponse ticket = new TicketResponseMapper(null).Map(dto);
//        TicketCompareHelper.Compare(ticket, dto);
//        Assert.assertNotNull(ticket.getBetDetails());
//        Assert.assertNotNull(dto.getResult().getBetDetails().stream().findFirst().get().getSelectionDetails());
//        Assert.assertNull(ticket.getBetDetails().stream().findFirst().get().getSelectionDetails());
//    }

    @Test
    public void BuildTicketResponseDtoFromJson4_MissingSelectionDetailsTest()
    {
        TicketResponseSchema dto = GetTicketResponseSchema("json/ticket-response4.json");
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getResult());
        Assert.assertEquals(Result.Status.REJECTED, dto.getResult().getStatus());
        Assert.assertNotNull(dto.getResult().getReason());
        Assert.assertEquals(Integer.valueOf(-321), dto.getResult().getReason().getCode());
        Assert.assertEquals(SdkInfo.MTS_TICKET_VERSION, dto.getVersion());
        Assert.assertNotNull(dto.getResult().getBetDetails());
        Assert.assertNotNull(dto.getResult().getBetDetails().stream().findFirst().get().getSelectionDetails());
        Assert.assertEquals(0, dto.getResult().getBetDetails().stream().findFirst().get().getSelectionDetails().size());
    }

//    @Test
//    public void BuildTicketResponseFromTicketResponseDto_MissingSelectionDetailsTest()
//    {
//        TicketResponseSchema dto = GetTicketResponseSchema("json/ticket-response4.json");
//        TicketResponse ticket = new TicketResponseMapper(null).Map(dto);
//        TicketCompareHelper.Compare(ticket, dto);
//        Assert.assertNotNull(ticket.getBetDetails());
//        Assert.assertNotNull(dto.getResult().getBetDetails().First().SelectionDetails);
//        Assert.assertNull(ticket.getBetDetails().First().SelectionDetails);
//    }

//    @Test
//    public void ValidateDtoEnumValuesTest()
//    {
//        Assert.assertEquals(com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelresponse.Result.Status.CANCELLED, 1);
//        Assert.assertEquals(TicketAckSchema.TicketStatus.ACCEPTED, 1);
//        Assert.assertEquals(TicketAckSchema.TicketStatus.REJECTED, 0);
//        Assert.assertEquals(TicketCancelAckSchema.TicketCancelStatus.CANCELLED, 1);
//        Assert.assertEquals(TicketCancelAckSchema.TicketCancelStatus.NOT_CANCELLED, 0);
//        Assert.assertEquals(Result.Status.ACCEPTED, 1);
//        Assert.assertEquals(Result.Status.REJECTED, 0);
//        Assert.assertEquals(com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelresponse.Result.Status.CANCELLED, 1);
//        Assert.assertEquals(com.sportradar.mts.sdk.api.impl.mtsdto.ticketcancelresponse.Result.Status.NOT_CANCELLED, 0);
//    }

    @Test
    public void BuildTicketResponseDtoFromJson_ReceivedFromCustomerTest()
    {
        TicketResponseSchema dto = GetTicketResponseSchema("json/ticket-response-customer.json");
        TicketResponse ticketResponse = MtsDtoMapper.map(dto, StaticRandom.S, null, "raw message");
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getResult());
        Assert.assertNotNull(ticketResponse);
        Assert.assertNotNull(ticketResponse.getBetDetails());
    }

    private TicketResponseSchema GetTicketResponseSchema(String filePath)
    {
        String json = new FileHelper().ReadFileToJson(filePath);
        byte[] jsonMsg = new FileHelper().ReadFileToByTe(filePath);
        TicketResponseSchema dto = null;

        try {
            dto = JsonUtils.deserialize(jsonMsg, TicketResponseSchema.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dto;
    }
}
